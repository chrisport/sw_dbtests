package com.cepe.dbFrameworkTest.ormLite;

import android.content.Context;

import com.cepe.dbFrameworkTest.ormLite.model.Person;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * dummy DAO-class, it is not necessary to build it
 * Created by chrisport on 27/12/13.
 */
public class PersonDAOBuilder{


    static AndroidConnectionSource connectionSource;
    public static Dao<Person,String> getPersonDAO(Context c) throws SQLException {
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(c);
        connectionSource = new AndroidConnectionSource(sqLiteHelper);

        TableUtils.createTableIfNotExists(connectionSource,Person.class);
        return DaoManager.createDao(connectionSource, Person.class);
    }

}


