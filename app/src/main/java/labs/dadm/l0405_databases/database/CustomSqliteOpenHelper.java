/*
 * Copyright (c) 2018. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0405_databases.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import labs.dadm.l0405_databases.pojo.Contact;

public class CustomSqliteOpenHelper extends SQLiteOpenHelper {

    // SQL sentence to create the contacts table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ContactContract.ContactEntry.TABLE_NAME + " (" +
                    ContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    ContactContract.ContactEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                    ContactContract.ContactEntry.COLUMN_NAME_EMAIL + " TEXT NOT NULL, " +
                    ContactContract.ContactEntry.COLUMN_NAME_PHONE + " TEXT NOT NULL)";

    // SQL sentence to remove the contacts table
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContactContract.ContactEntry.TABLE_NAME;

    // Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "contacts_database";

    // Singleton pattern to centralize access to the
    private static CustomSqliteOpenHelper ourInstance;

    public synchronized static CustomSqliteOpenHelper getInstance(Context context) {

        if (ourInstance == null) {
            ourInstance = new CustomSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        //  autoincremental integer primary key: _id
        //  String not null: name
        //  String not null: email
        //  String not null: phone
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /*
        This method is only called when the database needs to be upgraded,
        so it has been left blank
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /*
        Get List<HashMap<String,String>> object with all the contacts stored
        in the database to generate the data source to be later linked to a ListView:
    */
    public List<Contact> getContacts() {
        List<Contact> result = new ArrayList<>();
        Contact contact;

        // Get access to the database in read mode
        SQLiteDatabase database = getReadableDatabase();
        // Query the table to get the name, email, and phone for all existing entries
        Cursor cursor = database.query(
                ContactContract.ContactEntry.TABLE_NAME,
                new String[]{ContactContract.ContactEntry.COLUMN_NAME_NAME,
                        ContactContract.ContactEntry.COLUMN_NAME_EMAIL,
                        ContactContract.ContactEntry.COLUMN_NAME_PHONE},
                null,
                null,
                null,
                null,
                ContactContract.ContactEntry.COLUMN_NAME_NAME,
                null);
        // Go through the resulting cursor
        while (cursor.moveToNext()) {
            // Create Contact object for the given entry in the database
            contact = new Contact(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2));
            // Add the object to the result list
            result.add(contact);
        }
        // Close the cursor and database
        cursor.close();
        database.close();
        return result;
    }

    /*
        Insert a new contact into the database
    */
    public void addContact(Contact contact) {
        // Get access to the database in write mode
        SQLiteDatabase database = getWritableDatabase();
        // Insert the new contact into the table (autoincremental id)
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, contact.getEmail());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE, contact.getPhone());
        database.insert(ContactContract.ContactEntry.TABLE_NAME, null, values);
        // Close the database
        database.close();
    }

    /*
        Update the data of a given contact from the database
    */
    public void updateContact(String name, Contact contact) {
        // Get access to the database in write mode
        SQLiteDatabase database = getWritableDatabase();
        // Update the data from the contact identified by the given name
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, contact.getEmail());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE, contact.getPhone());
        database.update(ContactContract.ContactEntry.TABLE_NAME,
                values,
                ContactContract.ContactEntry.COLUMN_NAME_NAME + "=?",
                new String[]{name});
        // Close the database
        database.close();
    }

    /*
        Delete a given contact from the database
    */
    public void deleteContact(Contact contact) {
        // Get access to the database in write mode
        SQLiteDatabase database = getWritableDatabase();
        // Remove contacts from the database with matching name, email, and phone
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL, contact.getEmail());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE, contact.getPhone());
        database.delete(ContactContract.ContactEntry.TABLE_NAME,
                ContactContract.ContactEntry.COLUMN_NAME_NAME + "=? AND " +
                        ContactContract.ContactEntry.COLUMN_NAME_EMAIL + "=? AND + " +
                        ContactContract.ContactEntry.COLUMN_NAME_PHONE + "=?",
                new String[]{contact.getName(), contact.getEmail(), contact.getPhone()});
        // Close the database
        database.close();
    }

}
