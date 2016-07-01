package com.etamessenger.etamessengerproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by peter on 29/06/16.
 */
public class TripDBHelper extends SQLiteOpenHelper {

    private static TripDBHelper ourInstance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILENAME = "trips.db";
    public static final String TABLE_NAME = "Trips";
    Context context;

    public static TripDBHelper getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new TripDBHelper(context.getApplicationContext());
        }
        return ourInstance;
    }

    private TripDBHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // don't forget to use the column name '_id' for your primary key
    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            "  _id integer primary key autoincrement, " +
            "  name text not null, " +
            "  latCoord text not null, " +
            "  longCoord text not null, " +
            "  msgIDs text not null, " +
            "  contactIDs text not null " +
            ")";
    public static final String DROP_STATEMENT = "DROP TABLE " + TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // the implementation below is adequate for the first version
        // however, if we change our table at all, we'd need to execute code to move the data
        // to the new table structure, then delete the old tables (renaming the new ones)

        // the current version destroys all existing data
        database.execSQL(DROP_STATEMENT);
        database.execSQL(CREATE_STATEMENT);
    }

    private String generateIDString(ArrayList<? extends DBObject> list) {
        StringBuilder idlist = new StringBuilder();
        for (DBObject currElement : list){
            idlist.append(currElement.getId()).append("|");
        }
        return idlist.toString();
    }

    public Trip createTrip(Trip trip) {
        // create the object
//        Trip trip = new Trip(modules);

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", trip.getName());
        values.put("latCoord", trip.getLatCoord());
        values.put("longCoord", trip.getLongCoord());
        values.put("msgIDs", generateIDString(trip.getMessages()));
        values.put("contactIDs", generateIDString(trip.getContacts()));
        long id = database.insert(TABLE_NAME, null, values);

        // assign the Id of the new database row as the Id of the object
        trip.setId(id);
        return trip;
    }

    public Trip getTrip(long id) {
        Trip trip = null;

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the trip from the database
        String[] columns = new String[] {
                "name",
                "latCoord",
                "longCoord",
                "msgIDs",
                "contactIDs"
        };
        Cursor cursor = database.query(TABLE_NAME, columns, "_id = ?", new String[]{"" + id}, "", "", "");
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            MessageDBHelper msgDBHelper = MessageDBHelper.getInstance(context);
            ContactDBHelper contactDBHelper = ContactDBHelper.getInstance(context);

            trip = new Trip(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    msgDBHelper.getMessages(cursor.getString(4)),
                    contactDBHelper.getContacts(cursor.getString(5))
            );
            trip.setId(id);
        }
        Log.i("DatabaseAccess", "getTrip(" + id + "):  trip: " + trip);
        return trip;
    }

    public ArrayList<Trip> getAllTrips() {
        System.out.println("getting all trips");
        ArrayList<Trip> trips = new ArrayList<Trip>();

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the trip from the database
        String[] columns = new String[] { "_id",
                "name",
                "latCoord",
                "longCoord",
                "msgIDs",
                "contactIDs"
        };
        try {
            Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                do {
                    // collect the trip data, and place it into a trip object
                    long id = Long.parseLong(cursor.getString(0));
                    MessageDBHelper msgDBHelper = MessageDBHelper.getInstance(context);
                    ContactDBHelper contactDBHelper = ContactDBHelper.getInstance(context);
                    Trip trip = new Trip(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            msgDBHelper.getMessages(cursor.getString(4)),
                            contactDBHelper.getContacts(cursor.getString(5))
                    );
                    trip.setId(id);

                    // add the current trip to the list
                    trips.add(trip);

                    // advance to the next row in the results
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }
        } catch (SQLiteException databaseEmpty) {
            databaseEmpty.printStackTrace();
            Log.i("generating fake trip", "getAllTrips: ");
            this.createTrip(new Trip("School", "43.944677", "-78.89645", new ArrayList<Message>(), new ArrayList<Contact>()));
            return getAllTrips();
        }
        Log.i("DatabaseAccess", "getAllTrips():  num: " + trips.size());
        if (trips.size() == 0) {
            this.createTrip(new Trip("School", "43.944677", "-78.89645", new ArrayList<Message>(), new ArrayList<Contact>()));
            return getAllTrips();
        }
        return trips;
    }

    public boolean updateTrip(Trip trip) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // update the data in the database
        ContentValues values = new ContentValues();
        values.put("name", trip.getName());
        values.put("latCoord", trip.getLatCoord());
        values.put("longCoord", trip.getLongCoord());
        values.put("msgIDs", generateIDString(trip.getMessages()));
        values.put("contactIDs", generateIDString(trip.getContacts()));

        int numRowsAffected = database.update(TABLE_NAME, values, "_id = ?", new String[] { "" + trip.getId() });

        Log.i("DatabaseAccess", "updateTrip(" + trip + "):  numRowsAffected: " + numRowsAffected);

        // verify that the trip was updated successfully
        return (numRowsAffected == 1);
    }

    public boolean deleteTrip(long id) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the trip
        int numRowsAffected = database.delete(TABLE_NAME, "_id = ?", new String[]{"" + id});

        Log.i("DatabaseAccess", "deleteTrip(" + id + "):  numRowsAffected: " + numRowsAffected);

        // verify that the trip was deleted successfully
        return (numRowsAffected == 1);
    }

    public void deleteAllTrips() {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the trip
        int numRowsAffected = database.delete(TABLE_NAME, "", new String[] {});

        Log.i("DatabaseAccess", "deleteAllTrips():  numRowsAffected: " + numRowsAffected);
    }
}

