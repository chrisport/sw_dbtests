package com.cepe.dbFrameworkTest.ada;

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
import android.widget.TextView;

import com.cepe.dbFrameworkTest.R;
import com.cepe.dbFrameworkTest.ada.model.Person;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.ArrayList;
import java.util.List;

public class AdaActivity extends ActionBarActivity {
    protected PersonDAO personDAO;
    protected boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        resetDb();
    }

    public void resetDb() {
        try {
            personDAO = new PersonDAO(this.getApplicationContext());

            //empty existing database
            personDAO.personSet.fill();
            personDAO.personSet.removeAll();
            personDAO.personSet.save();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    /**
     * runs a concurrency test
     */
    public void runConcurrencyTest() throws InterruptedException, AdaFrameworkException {
        this.resetDb();
        Log.i("ADA TEST", "TESTCASE B");

        //start 20 threads which write to same table
        long time = System.currentTimeMillis();
        Thread[] threads = new Thread[20];
        for (int i = 0; i < 20; i++) {
            final int k = i;
            threads[k] = new Thread() {
                @Override
                public void run() {
                    com.cepe.dbFrameworkTest.ada.model.Person person = new com.cepe.dbFrameworkTest.ada.model.Person();
                    person.setFirstName("Christoph" + k);
                    person.setLastName("Portmann" + k);
                    try {
                        PersonDAO personDao2;

                        if (k % 2 == 0) {
                            personDao2 = new PersonDAO(AdaActivity.this.getApplicationContext());
                        } else {
                            personDao2 = new PersonDAO(AdaActivity.this);
                        }
                        personDao2.personSet.add(person);
                        personDao2.personSet.save();
                    } catch (AdaFrameworkException e) {
                        e.printStackTrace();
                    }

                }


            };
            threads[k].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        personDAO.personSet.clear();
        personDAO.personSet.fill();
        Log.i("ADA TEST", "Test passed: " + (personDAO.personSet.size() == 20) + " time needed: " + (System.currentTimeMillis() - time));
    }

    /**
     * runs a performance test
     */
    public void runPerformanceTest() {
        this.resetDb();
        try {
            Log.i("ADA TEST", "TESTCASE A");

            long time = System.currentTimeMillis();
            this.populateAdaDb();
            Log.i("ADA TEST", "time for creating database " + (System.currentTimeMillis() - time) +"ms");
            time = System.currentTimeMillis();
            List<Person> portmannLastName = personDAO.getPersonByLastNameManually("Portmann");
            Log.i("ADA TEST", "time for searching in 10000 rows database " + (System.currentTimeMillis() - time)+"ms");
            portmannLastName.get(0).setFirstName("Hans");
            personDAO.personSet.save(portmannLastName.get(0));
            Log.i("ADA TEST", "time for change and save back " + (System.currentTimeMillis() - time)+"ms");
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    /**
     * populates the database with 10000 names + 1 predefined name
     */
    private void populateAdaDb() {
        List<Person> people = new ArrayList<Person>(10000);
        for (int i = 0; i < 10000; i++) {
            com.cepe.dbFrameworkTest.ada.model.Person person2 = new com.cepe.dbFrameworkTest.ada.model.Person();
            person2.setFirstName("notChristoph" + i);
            person2.setLastName("notPortmann" + i);
            people.add(person2);
        }
        personDAO.personSet.addAll(people);

        com.cepe.dbFrameworkTest.ada.model.Person person = new com.cepe.dbFrameworkTest.ada.model.Person();
        person.setFirstName("Christoph");
        person.setLastName("Portmann");
        personDAO.personSet.add(person);

        try {
            personDAO.personSet.save();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        TextView resultTV;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button adaFramework = (Button) rootView.findViewById(R.id.button);
            adaFramework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdaActivity.this.runPerformanceTest();
                }
            });
            Button adaFramework2 = (Button) rootView.findViewById(R.id.button2);
            adaFramework2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        AdaActivity.this.runConcurrencyTest();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (AdaFrameworkException e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }
    }

}
