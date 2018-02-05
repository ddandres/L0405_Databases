/*
 * Copyright (c) 2018. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.sdm.l0405_databases.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomSqliteOpenHelper extends SQLiteOpenHelper {

    // Singleton pattern to centralize access to the
    private static CustomSqliteOpenHelper ourInstance;

    public synchronized static CustomSqliteOpenHelper getInstance(Context context) {

        if (ourInstance == null) {
            ourInstance = new CustomSqliteOpenHelper(context, "contacts_database", null, 1);
        }
        return ourInstance;

    }

    /*
        Create a helper object to manage a database
        Parameters:
          context
          filename of the database, or null for in-memory database
          factory to create cursor objects, default if null
          version of the database (upgrades/downgrades existing ones)
    */
    private CustomSqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /*
        This method is only called to create the database the first time it is accessed
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create a contacts_table table with
        //  autoincremental integer primary key: id
        //  String not null: name
        //  String not null: email
        //  String not null: phone
        db.execSQL("CREATE TABLE contacts_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "name TEXT NOT NULL, email TEXT NOT NULL, phone TEXT NOT NULL);");
    }

    /*
        This method is only called when the database needs to be upgraded,
        so it has been left blank
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*
        Get List<HashMap<String,String>> object with all the contacts stored
        in the database to generate the data source to be later linked to a ListView:
    */
    public List<HashMap<String, String>> getContacts() {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> map;

        // Get access to the database in read mode
        SQLiteDatabase database = getReadableDatabase();
        // Query the table to get the name, email, and phone for all existing entries
        Cursor cursor = database.query("contacts_table", new String[]{"name", "email", "phone"},
                null,null, null, null, "name", null);
        // Go through the resulting cursor
        while (cursor.moveToNext()) {
            // Create a HashMap<String,String> object for the given entry in the database
            map = new HashMap<>();
            map.put("name", cursor.getString(0));
            map.put("email", cursor.getString(1));
            map.put("phone", cursor.getString(2));
            // Add the object to the result list
            result.add(map);
        }
        // Close the cursor and database
        cursor.close();
        database.close();
        return result;
    }

    /*
        Insert a new contact into the database
    */
    public void addContact(String name, String email, String phone) {
        // Get access to the database in write mode
        SQLiteDatabase database = getWritableDatabase();
        // Insert the new contact into the table (autoincremental id)
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        database.insert("contacts_table", null, values);
        // Close the database
        database.close();
    }

    /*
        Update the data a given contact from the database
    */
    public void updateContact (String name, String email, String phone) {
        // Get access to the database in write mode
        SQLiteDatabase database = getWritableDatabase();
        // Update the data from the contact identified by the given name
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        database.update("contacts_table", values, "name=?", new String[]{name});
        // Close the database
        database.close();
    }

    /*
        Delete a given contact from the database
    */
    public void deleteContact(String name, String email, String phone) {
        // Get access to the database in write mode
        SQLiteDatabase database = getWritableDatabase();
        // Remove contacts from the database with matching name, email, and phone
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        database.delete("contacts_table", "name=? AND email=? and phone=?",
                        new String[]{name, email, phone});
        // Close the database
        database.close();
    }
}
