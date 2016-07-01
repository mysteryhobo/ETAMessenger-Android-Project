package com.etamessenger.etamessengerproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

public class TripsActivity extends AppCompatActivity {
    private RecyclerView rv;
    private RecyclerView.Adapter rvAdapter;
    private LinearLayoutManager rvManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        rv = (RecyclerView) findViewById(R.id.tripList);
        rv.setHasFixedSize(true);
        rvManager = new LinearLayoutManager(getApplication());
        rvManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(rvManager);

//        TripDBHelper dbHelper = TripDBHelper.getInstance(this);
//        ArrayList<Trip> trips = dbHelper.getAllTrips();
//        rvAdapter = new TripItemAdapter(trips, getApplication());
//        rv.setAdapter(rvAdapter);

//        System.out.println("Number of trips: " + trips.size());
//        for (Trip currTrip : trips) {
//            System.out.println(currTrip.toString());
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(Html.fromHtml("<font color='#ff0000'>ActionBarTitle </font>"));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        TripDBHelper dbHelper = TripDBHelper.getInstance(this);
        ArrayList<Trip> trips = dbHelper.getAllTrips();
        rvAdapter = new TripItemAdapter(trips, getApplication());
        rv.setAdapter(rvAdapter);
    }
}
