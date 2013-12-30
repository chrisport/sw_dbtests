package com.cepe.dbFrameworkTest.ormLite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cepe.dbFrameworkTest.R;
import com.cepe.dbFrameworkTest.ormLite.model.Person;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class ORMLiteActivity extends ActionBarActivity {
    Dao<Person, String> personDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ormlite);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        try {

            personDao = PersonDAOBuilder.getPersonDAO(this);
            this.resetDb();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void resetDb() throws SQLException {
        List<Person> people = personDao.queryForAll();
        int before = people.size();
        TableUtils.clearTable(PersonDAOBuilder.connectionSource,Person.class);

        people = personDao.queryForAll();
        Log.i("ORMLite TEST", "#objects found: " + people.size() + " before: " + before);
    }

    private void runPerformanceTest() throws SQLException {
        Log.i("ORMLITE TEST", "TESTCASE A");
        this.resetDb();

        long time = System.currentTimeMillis();
        this.populateDb();
        Log.i("ORMLite TEST", "time for creating database " + (System.currentTimeMillis() - time)+"ms");
        time = System.currentTimeMillis();
        Person pers = personDao.queryForId("Christoph");
        Log.i("ORMLite TEST", "time for searching in 10000 rows " + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        pers.setLastName("Hans");
        personDao.update(pers);
        Log.i("ORMLite TEST", "time for change and save back " + (System.currentTimeMillis() - time)+"ms");

    }

    /**
     * populates database with 10000 entries
     * @throws SQLException
     */
    public void populateDb() throws SQLException {


        try {
            personDao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (int i = 0; i < 10000; i++) {
                        Person p = new Person();

                        p.setFirstName("notChristoph" + i);
                        p.setLastName("notPortmann" + i);
                        personDao.createIfNotExists(p);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Person p2 = new Person();
        p2.setFirstName("Christoph");
        p2.setLastName("Portmann");
        personDao.createOrUpdate(p2);
    }


    /**
     * runs a concurrency test
     */
    public void runConcurrencyTest() throws InterruptedException, SQLException {
        Log.i("ORMLITE TEST", "TESTCASE B");
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
                    try {
                        Dao<Person, String> personDao = PersonDAOBuilder.getPersonDAO(ORMLiteActivity.this);
                        personDao.create(person);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            threads[k].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        Log.i("ORMLite TEST", "Test passed: "+(personDao.queryForAll().size()==20)+" time needed " + (System.currentTimeMillis() - time));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ormlite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

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
                    try {
                        ORMLiteActivity.this.runPerformanceTest();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            Button adaFramework2 = (Button) rootView.findViewById(R.id.button2);
            adaFramework2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ORMLiteActivity.this.runConcurrencyTest();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }
    }

}
