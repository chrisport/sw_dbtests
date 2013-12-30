package com.cepe.dbFrameworkTest.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cepe.dbFrameworkTest.R;
import com.cepe.dbFrameworkTest.greenDao.gen.DaoMaster;
import com.cepe.dbFrameworkTest.greenDao.gen.DaoSession;
import com.cepe.dbFrameworkTest.greenDao.gen.Person;
import com.cepe.dbFrameworkTest.greenDao.gen.PersonDao;

import java.util.ArrayList;
import java.util.List;

public class GdaoActivity extends ActionBarActivity {
    private SQLiteDatabase db;

    private EditText editText;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private PersonDao personDao;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        personDao = createPersonDao();
        resetDb();
    }

    public PersonDao createPersonDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        return daoSession.getPersonDao();
    }

    public void resetDb() {
        personDao.deleteAll();
    }

    private void runPerformanceTest() {
        Log.i("GreenDao TEST", "TESTCASE A");
        this.resetDb();
        long time = System.currentTimeMillis();
        this.populateDb();

        Log.i("GreenDao TEST", "time for creating database " + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        List<Person> query = personDao.queryBuilder().where(PersonDao.Properties.FirstName.eq("Christoph")).list();
        Person pers = query.get(0);
        Log.i("GreenDao TEST", "time for searching in 10000 rows " + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        pers.setLastName("Hans");
        personDao.update(pers);
        Log.i("GreenDao TEST", "time for change and save back " + (System.currentTimeMillis() - time) + "ms");
    }

    public void populateDb() {
        List<Person> people = new ArrayList<Person>();
        for (int i = 0; i < 10000; i++) {
            Person p = new Person();

            p.setFirstName("notChristoph" + i);
            p.setLastName("notPortmann" + i);
            people.add(p);
        }
        personDao.insertOrReplaceInTx(people);

        Person p2 = new Person();
        p2.setFirstName("Christoph");
        p2.setLastName("Portmann");
        personDao.insertOrReplace(p2);
    }


    /**
     * runs a concurrency test
     */
    public void runConcurrencyTest() throws InterruptedException {
        Log.i("GreenDao TEST", "TESTCASE b");
        this.resetDb();
        //start 20 threads which write to same table
        long time = System.currentTimeMillis();
        Thread[] threads = new Thread[20];
        for (int i = 0; i < 20; i++) {
            final int k = i;

            threads[k] = new Thread() {
                @Override
                public void run() {
                    Person person = new Person();
                    person.setFirstName("Christoph" + k);
                    person.setLastName("Portmann" + k);
                    PersonDao cDao = createPersonDao();
                    cDao.insertOrReplace(person);
                    cDao.getDatabase().close();
                }
            };
            threads[k].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        Log.i("GreenDao TEST", "Test passed: " + (personDao.count() == 20) + " time needed: " + (System.currentTimeMillis() - time));
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        TextView resultTV;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ormlite, container, false);

            Button adaFramework = (Button) rootView.findViewById(R.id.button);
            adaFramework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GdaoActivity.this.runPerformanceTest();
                }
            });
            Button adaFramework2 = (Button) rootView.findViewById(R.id.button2);
            adaFramework2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        GdaoActivity.this.runConcurrencyTest();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }
    }
}