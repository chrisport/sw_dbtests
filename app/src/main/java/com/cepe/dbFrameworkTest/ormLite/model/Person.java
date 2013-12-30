package com.cepe.dbFrameworkTest.ormLite.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mobandme.ada.Entity;
/**
 * Created by chrisport on 27/12/13.
 */
@DatabaseTable(tableName = "Person")
public class Person extends Entity {
    @DatabaseField(id = true)
    private String firstName;

    @DatabaseField
    private String lastName;

    public Person(){

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
