
/*
 * Copyright (c) 2018. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0405_databases.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import labs.dadm.l0405_databases.R;
import labs.dadm.l0405_databases.adapters.ContactAdapter;
import labs.dadm.l0405_databases.database.CustomSqliteOpenHelper;
import labs.dadm.l0405_databases.pojo.Contact;


public class MainActivity extends AppCompatActivity {

    // Constants identifying the current state in edition mode
    final static int STATE_NONE = 0;
    final static int STATE_NEW = 1;
    final static int STATE_EDIT = 2;

    // Adapter object linking the data source and the ListView
    ContactAdapter adapter = null;

    // Hold references to View objects
    EditText etName = null;
    EditText etEmail = null;
    EditText etPhone = null;
    ImageButton ibSend = null;
    ImageButton ibCall = null;

    // Current state of edition
    int state = STATE_NONE;
    // Position of the element selected from the list
    int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep a reference to:
        //  the EditText displaying the name of the contact
        //  the EditText displaying the email address of the contact
        //  the EditText displaying the phone number of the contact
        //  the ImageButton for sending a message to the contact
        //  the ImageButton for calling the contact
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        ibSend = findViewById(R.id.bSend);
        ibCall = findViewById(R.id.bCall);

        // App is not in edition mode
        disableEdition();

        findViewById(R.id.bSend).setOnClickListener(v -> sendMessage());
        findViewById(R.id.bCall).setOnClickListener(v -> callContact());

        // Reference to the ListView object displaying the contacts
        final ListView list = findViewById(R.id.lvAgenda);
        // When an item in the list is clicked
        // enable the edition mode and display the contact's data
        list.setOnItemClickListener((parent, view, position, id) -> displayContactData(position));

        // Get all contacts stored in the database
        final List<Contact> contactList = CustomSqliteOpenHelper.getInstance(this).getContacts();

        // Create the adapter linking the data source to the ListView
        adapter = new ContactAdapter(this, R.layout.list_item, contactList);

        // Set the data behind this ListView
        list.setAdapter(adapter);
    }

    // Enables the edition mode and displays the contact's data
    private void displayContactData(int position) {
        // Enter edition mode
        enableEdition();
        // Update EditTexts with contact's data
        final Contact contact = (Contact) adapter.getItem(position);
        etName.setText(contact.getName());
        etEmail.setText(contact.getEmail());
        etPhone.setText(contact.getPhone());

        // Remember the position of the selected object form the list
        selectedPosition = position;

        // Remember the app is in edition mode
        state = STATE_EDIT;
        // Update action buttons in the ActionBar
        supportInvalidateOptionsMenu();
    }

    // This method is executed when the activity is created to populate the ActionBar with actions
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Generate the Menu object from the XML resource file
        getMenuInflater().inflate(R.menu.main, menu);
        // Make actions visible according to the app's edition mode
        switch (state) {
            case STATE_NONE:
                menu.findItem(R.id.action_new).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_clear).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(false);
                break;
            case STATE_NEW:
                menu.findItem(R.id.action_new).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_clear).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
                break;
            case STATE_EDIT:
                menu.findItem(R.id.action_new).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_clear).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(true);
                break;
        }

        return true;
    }

    // This method is executed when any action from the ActionBar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Determine the action to take place according to the Id of the action selected
        final int selectedItem = item.getItemId();
        if (selectedItem == R.id.action_new) {
            // Prepare the interface for introducing a new contact
            prepareInterfaceForNewContact();
            return true;
        } else if (selectedItem == R.id.action_save) {
            // Store the data under modification

            // The contact's name is mandatory to create a new contact
            if (etName.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(this, R.string.name_required, Toast.LENGTH_SHORT).show();
            } else {
                storeData();
            }
            return true;
        } else if (selectedItem == R.id.action_clear) {
            // Clear data fields and stop editing
            cancelEdition();
            return true;
        } else if (selectedItem == R.id.action_delete) {
            // Delete the contact from the database
            deleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Deletes the contact from the database
    private void deleteContact() {
        // Get the data of the selected contact
        final Contact contact = (Contact) adapter.getItem(selectedPosition);
        // Delete the contact form the database
        CustomSqliteOpenHelper.getInstance(this).deleteContact(contact);
        // Remove the contact from the list
        adapter.remove(contact);
        // Clear the data fields
        clearEdition();
        // Stop editing
        disableEdition();
        // App is not in edition mode
        state = STATE_NONE;
        // Update action buttons in the ActionBar
        supportInvalidateOptionsMenu();
    }

    // Clears data fields and stops editing
    private void cancelEdition() {
        // If creating a new contact, then clear the data fields
        if (state == STATE_NEW) {
            // Clear the data fields
            clearEdition();
        }
        // If editing an existing contact, then clear the data fields and stop editing
        else if (state == STATE_EDIT) {
            // Clear the data fields
            clearEdition();
            // Stop editing
            disableEdition();
            state = STATE_NONE;
            // Update action buttons in the ActionBar
            supportInvalidateOptionsMenu();
        }
    }

    // Stores the data under modification
    private void storeData() {
        // Get all the information from the data fields
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String phone = etPhone.getText().toString();

        // If creating a new contact, then add it to the list and database
        if (state == STATE_NEW) {
            // Create a new contact
            final Contact contact = new Contact(name, email, phone);
            contact.set_ID(CustomSqliteOpenHelper.getInstance(this).addContact(contact));
            adapter.add(contact);
        }
        // If editing an existing contact, then update the list and database
        else if (state == STATE_EDIT) {
            final Contact contact = (Contact) adapter.getItem(selectedPosition);
            contact.setName(name);
            contact.setEmail(email);
            contact.setPhone(phone);
            CustomSqliteOpenHelper.getInstance(this).updateContact(contact);
        }

        // Clear the data fields
        clearEdition();
        // Stop editing
        disableEdition();
        // App is not in edition mode
        state = STATE_NONE;
        // Update action buttons in the ActionBar
        supportInvalidateOptionsMenu();
    }

    // Prepares the interface to introduce the data of a new contact
    private void prepareInterfaceForNewContact() {
        // Enable edition
        enableEdition();
        // Clear the data fields
        clearEdition();
        // App is now editing a new contact
        state = STATE_NEW;
        // Update action buttons in the ActionBar
        supportInvalidateOptionsMenu();
    }

    // Enables all View in edition mode
    private void enableEdition() {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
        ibSend.setEnabled(true);
        ibCall.setEnabled(true);
    }

    // Disables all View when not in edition mode
    private void disableEdition() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);
        ibSend.setEnabled(false);
        ibCall.setEnabled(false);
    }

    // Clears all data fields
    private void clearEdition() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }

    // Sends message
    private void sendMessage() {
        // Create an implicit Intent
        Intent intent = new Intent();
        // Complete Intent information to send a message to the contact's email address
        intent.setAction(Intent.ACTION_SEND);
        intent.setDataAndType(Uri.parse("mailto:"), "text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{etEmail.getText().toString()});
        // Use a chooser to select which application will handle the task
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    private void callContact() {
        // Create an implicit Intent
        Intent intent = new Intent();
        // Complete Intent information to dial the contact's phone number
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + etPhone.getText().toString()));
        // Start dialer
        startActivity(intent);
    }
}
