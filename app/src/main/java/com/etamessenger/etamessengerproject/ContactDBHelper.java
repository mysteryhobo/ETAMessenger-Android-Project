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
public class ContactDBHelper extends SQLiteOpenHelper{

    private static ContactDBHelper ourInstance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILENAME = "contacts.db";
    public static final String TABLE_NAME = "Contacts";
    Context context;

    public static ContactDBHelper getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ContactDBHelper(context.getApplicationContext());
        }
        return ourInstance;
    }

    private ContactDBHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // don't forget to use the column name '_id' for your primary key
    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            "  _id integer primary key autoincrement, " +
            "  name text not null, " +
            "  number text not null " +
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

    private long[] parseIDString(String idString) {
        if (idString == null || idString.length() == 0) return new long[0];
        String[] listofIDStrings = idString.split("|");
        long[] listofIds = new long[listofIDStrings.length];
        for (int i = 0; i < listofIds.length; i ++) {
            listofIds[i] = Long.parseLong(listofIDStrings[i], 10);
        }
        return listofIds;
    }

    public Contact createContact(Contact contact) {
        // create the object
//        Contact contact = new Contact(modules);

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("number", contact.getNumber());
        long id = database.insert(TABLE_NAME, null, values);

        // assign the Id of the new database row as the Id of the object
        contact.setId(id);
        return contact;
    }

    public Contact getContact(long id) {
        Contact contact = null;

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the contact from the database
        String[] columns = new String[] {
                "name",
                "number"
        };
        Cursor cursor = database.query(TABLE_NAME, columns, "_id = ?", new String[]{"" + id}, "", "", "");
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();

            contact = new Contact(
                    cursor.getString(1),
                    cursor.getString(2)
            );
            contact.setId(id);
        }
        Log.i("DatabaseAccess", "getContact(" + id + "):  contact: " + contact);
        return contact;
    }

    public ArrayList<Contact> getContacts(String contactIds) {
        ArrayList<Contact> contacts = new ArrayList<>();
        long[] ids = parseIDString(contactIds);

        for (Long currentId : ids) {
            contacts.add(getContact(currentId));
        }
        return contacts;
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the contact from the database
        String[] columns = new String[] { "_id",
                "name",
                "number"
        };
        try {
            Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                do {
                    // collect the contact data, and place it into a contact object
                    long id = Long.parseLong(cursor.getString(0));
                    Contact contact = new Contact(
                            cursor.getString(1),
                            cursor.getString(2)
                    );
                    contact.setId(id);

                    // add the current contact to the list
                    contacts.add(contact);

                    // advance to the next row in the results
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }
        } catch (SQLiteException databaseEmpty) {
            databaseEmpty.printStackTrace();
        }
        Log.i("DatabaseAccess", "getAllContacts():  num: " + contacts.size());
        return contacts;
    }

    public boolean updateContact(Contact contact) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // update the data in the database
        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("number", contact.getNumber());

        int numRowsAffected = database.update(TABLE_NAME, values, "_id = ?", new String[] { "" + contact.getId() });

        Log.i("DatabaseAccess", "updateContact(" + contact + "):  numRowsAffected: " + numRowsAffected);

        // verify that the contact was updated successfully
        return (numRowsAffected == 1);
    }

    public boolean deleteContact(long id) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the contact
        int numRowsAffected = database.delete(TABLE_NAME, "_id = ?", new String[]{"" + id});

        Log.i("DatabaseAccess", "deleteContact(" + id + "):  numRowsAffected: " + numRowsAffected);

        // verify that the contact was deleted successfully
        return (numRowsAffected == 1);
    }

    public void deleteAllContacts() {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the contact
        int numRowsAffected = database.delete(TABLE_NAME, "", new String[] {});

        Log.i("DatabaseAccess", "deleteAllContacts():  numRowsAffected: " + numRowsAffected);
    }
}


