package com.cepe.dbFrameworkTest.ada;

import android.content.Context;
import android.util.Log;

import com.cepe.dbFrameworkTest.ada.model.Person;
import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.ObjectSet;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrisport on 27/12/13.
 */
public class PersonDAO extends ObjectContext {

    public ObjectSet<Person> personSet;

    public PersonDAO(Context pContext) throws AdaFrameworkException {
        super(pContext);
        this.personSet = new ObjectSet<Person>(Person.class, this);
        this.personSet.getElementByID(3l);
       // this.personSet.fill();
    }

    public void printAll() {
        for (Person p : personSet)
            Log.i("asd", p.getFirstName());

    }

    /**
     * SELECT example
     *
     * @param lastName
     * @return
     */
    public List<Person> getPersonByLastName(String lastName) {
        List<Person> result = new ArrayList<Person>();

        try {
            result = personSet.search(false, "lastName", new String[]{lastName}, null, null, null, null, null);
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Person> getPersonByLastNameManually(String lastName) {
        List<Person> result = new ArrayList<Person>();

        for (Person p : personSet) {
            if (p.getLastName().equals(lastName)) {
                result.add(p);
            }
        }
        return result;
    }
}


