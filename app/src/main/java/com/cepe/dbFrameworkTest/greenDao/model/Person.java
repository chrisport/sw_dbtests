package com.cepe.dbFrameworkTest.greenDao.model;


import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

/**
 * Created by chrisport on 27/12/13.
 */
@Table(name = "Person")
public class Person extends Entity {
    @TableField(name = "firstName", datatype = Entity.DATATYPE_TEXT, required = true, maxLength = 100)
    private String firstName;
    @TableField(name = "lastName", datatype = Entity.DATATYPE_TEXT, required = true, maxLength = 100)
    private String lastName;


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
