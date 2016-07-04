package com.etamessenger.etamessengerproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;

/**
 * Created by peter on 29/06/16.
 */
public class MessageDBHelper extends SQLiteOpenHelper {

    private static MessageDBHelper ourInstance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILENAME = "message.db";
    public static final String TABLE_NAME = "Messages";
    Context context;

    public static MessageDBHelper getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MessageDBHelper(context.getApplicationContext());
        }
        return ourInstance;
    }

    private MessageDBHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // don't forget to use the column name '_id' for your primary key
    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            "  _id integer primary key autoincrement, " +
            "  msgText text not null, " +
            "  msgTime int not null " +
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
        Log.i("messageDB", "idString:" + idString);
        String[] listofIDStrings = idString.split("_");
        Log.i("messageDB", "arraylength:" + listofIDStrings.length);
        long[] listofIds = new long[listofIDStrings.length];
        for (int i = 0; i < listofIds.length; i ++) {
            Log.i("messageDB", "String: " + i + " is: |" + listofIDStrings[i] + "|");
            listofIds[i] = Long.parseLong(listofIDStrings[i], 10);
        }
        return listofIds;
    }

    public Message createMessage(Message message) {
        // create the object
//        Message message = new Message(modules);

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("msgText", message.getMessageText());
        values.put("msgTime", message.getMessageTime());
        long id = database.insert(TABLE_NAME, null, values);

        // assign the Id of the new database row as the Id of the object
        message.setId(id);
        return message;
    }

    public Message getMessage(long id) {
        Message message = null;

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the message from the database
        String[] columns = new String[] {
                "msgText",
                "msgTime"
        };
        Cursor cursor = database.query(TABLE_NAME, columns, "_id = ?", new String[]{"" + id}, "", "", "");
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();

            message = new Message(
                    cursor.getString(0),
                    cursor.getInt(1)
            );
            message.setId(id);
        }
        Log.i("DatabaseAccess", "getMessage(" + id + "):  message: " + message);
        return message;
    }

    public ArrayList<Message> getMessages(String messageIds) {
        ArrayList<Message> messages = new ArrayList<>();
        long[] ids = parseIDString(messageIds);

        for (Long currentId : ids) {
            messages.add(getMessage(currentId));
        }
        return messages;
    }

    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> messages = new ArrayList<>();

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the message from the database
        String[] columns = new String[] { "_id",
                "msgText",
                "msgTime"
        };
        try {
            Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                do {
                    // collect the message data, and place it into a message object
                    long id = Long.parseLong(cursor.getString(0));
                    Message message = new Message(
                            cursor.getString(1),
                            cursor.getInt(2)
                    );
                    message.setId(id);

                    // add the current message to the list
                    messages.add(message);

                    // advance to the next row in the results
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }
        } catch (SQLiteException databaseEmpty) {
            databaseEmpty.printStackTrace();
        }
        Log.i("DatabaseAccess", "getAllMessages():  num: " + messages.size());
        return messages;
    }

    public boolean updateMessage(Message message) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // update the data in the database
        ContentValues values = new ContentValues();
        values.put("msgText", message.getMessageText());
        values.put("msgTime", message.getMessageTime());

        int numRowsAffected = database.update(TABLE_NAME, values, "_id = ?", new String[] { "" + message.getId() });

        Log.i("DatabaseAccess", "updateMessage(" + message + "):  numRowsAffected: " + numRowsAffected);

        // verify that the message was updated successfully
        return (numRowsAffected == 1);
    }

    public boolean deleteMessage(long id) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the message
        int numRowsAffected = database.delete(TABLE_NAME, "_id = ?", new String[]{"" + id});

        Log.i("DatabaseAccess", "deleteMessage(" + id + "):  numRowsAffected: " + numRowsAffected);

        // verify that the message was deleted successfully
        return (numRowsAffected == 1);
    }

    public void deleteAllMessages() {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the message
        int numRowsAffected = database.delete(TABLE_NAME, "", new String[] {});

        Log.i("DatabaseAccess", "deleteAllMessages():  numRowsAffected: " + numRowsAffected);
    }
}

