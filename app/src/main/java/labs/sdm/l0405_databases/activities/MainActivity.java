
/*
 * Copyright (c) 2018. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.sdm.l0405_databases.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import labs.sdm.l0405_databases.R;
import labs.sdm.l0405_databases.database.CustomSqliteOpenHelper;


public class MainActivity extends AppCompatActivity {

    // Constants identifying the current state in edition mode
    final static int STATE_NONE = 0;
    final static int STATE_NEW = 1;
    final static int STATE_EDIT = 2;

    // Data source for contacts
    List<HashMap<String, String>> contactList = null;
    // Adapter object linking the data source and the ListView
    SimpleAdapter adapter = null;

    // Hold references to View objects
    EditText etName = null;
    EditText etEmail = null;
    EditText etPhone = null;
    ImageButton ibSend = null;
    ImageButton ibCall = null;

    // Current state of edition
    int state = STATE_NONE;
    // Position of the element selected from the list
    int itemSelected = 0;

    CustomSqliteOpenHelper helper;

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

        // Reference to the ListView object displaying the contacts
        ListView list = findViewById(R.id.lvAgenda);
        // When an item in the list is clicked
        // enable the edition mode and display the contact's data
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Enter edition mode
                enableEdition();
                // Update EditTexts with contact's data
                etName.setText(contactList.get(position).get("name"));
                etEmail.setText(contactList.get(position).get("email"));
                etPhone.setText(contactList.get(position).get("phone"));

                // Remember the position of the selected object form the list
                itemSelected = position;

                // Remember the app is in edition mode
                state = STATE_EDIT;
                // Update action buttons in the ActionBar
                supportInvalidateOptionsMenu();
            }
        });

        // Instance of CustomSqliteOpenHelper to perform operation on the database
        helper = CustomSqliteOpenHelper.getInstance(this);

        // Get all contacts stored in the database
        contactList = helper.getContacts();

        // Create the adapter linking the data source to the ListView
        adapter = new SimpleAdapter(
                this,
                contactList,
                R.layout.list_item, new String[]{"name", "email", "phone"},
                new int[]{R.id.tvName, R.id.tvEmail, R.id.tvPhone}
        );
        // Set the data behind this ListView
        list.setAdapter(adapter);
    }

    /*
        This method is executed when the activity is created to populate the ActionBar with actions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    /*
        This method is executed when any action from the ActionBar is selected
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Determine the action to take place according to the Id of the action selected
        switch (item.getItemId()) {

            // Prepare the interface to introduce the data of a new contact
            case R.id.action_new:
                // Enable edition
                enableEdition();
                // Clear the data fields
                clearEdition();
                // App is now editing a new contact
                state = STATE_NEW;
                // Update action buttons in the ActionBar
                supportInvalidateOptionsMenu();
                return true;

            // Store the data under modification
            case R.id.action_save:

                // The contact's name is mandatory to create a new contact
                if (etName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(this, R.string.name_required, Toast.LENGTH_SHORT).show();
                }
                else {
                    // Get all the information from the data fields
                    final String name = etName.getText().toString();
                    final String email = etEmail.getText().toString();
                    final String phone = etPhone.getText().toString();
                    // Create a new object for the List
                    final HashMap<String, String> contact = new HashMap<>();
                    contact.put("name", name);
                    contact.put("email", email);
                    contact.put("phone", phone);

                    // If creating a new contact, then add it to the list and database
                    if (state == STATE_NEW) {
                        contactList.add(contact);
                        helper.addContact(name, email, phone);
                    }
                    // If editing an existing contact, then update the list and database
                    else if (state == STATE_EDIT) {
                        contactList.set(itemSelected, contact);
                        helper.updateContact(name, email, phone);
                    }
                    // Notify the adapter to update the ListView since its data source has changed
                    adapter.notifyDataSetChanged();

                    // Clear the data fields
                    clearEdition();
                    // Stop editing
                    disableEdition();
                    // App is not in edition mode
                    state = STATE_NONE;
                    // Update action buttons in the ActionBar
                    supportInvalidateOptionsMenu();
                }
                return true;

            // Clear data fields and stop editing
            case R.id.action_clear:
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
                return true;

            // Delete the contact from the database
            case R.id.action_delete:
                // Get the data of the selected contact
                final HashMap<String, String> contact = contactList.get(itemSelected);
                // Delete the contact form the database
                helper.deleteContact(contact.get("name"), contact.get("email"), contact.get("phone"));
                // Remove the contact from the list
                contactList.remove(itemSelected);
                // Notify the adapter to update the ListView since its data source has changed
                adapter.notifyDataSetChanged();
                // Clear the data fields
                clearEdition();
                // Stop editing
                disableEdition();
                // App is not in edition mode
                state = STATE_NONE;
                // Update action buttons in the ActionBar
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        Enable all View in edition mode
     */
    private void enableEdition() {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
        ibSend.setEnabled(true);
        ibCall.setEnabled(true);
    }

    /*
        Disable all View when not in edition mode
     */
    private void disableEdition() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);
        ibSend.setEnabled(false);
        ibCall.setEnabled(false);
    }

    /*
        Clear all data fields
     */
    private void clearEdition() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }

    /*
        This method is activated when either the send message or call buttons are clicked
     */
    public void onClickButton(View v) {
        // Create an implicit Intent
        Intent intent = new Intent();
        // Determine the action to perform
        switch (v.getId()) {
            // Send message
            case R.id.bSend:
                // Complete Intent information to send a message to the contact's email address
                intent.setAction(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{etEmail.getText().toString()});
                // Use a chooser to select which application will handle the task
                startActivity(Intent.createChooser(intent, "Send email..."));
                break;
            // Call
            case R.id.bCall:
                // Complete Intent information to dial the contact's phone number
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + etPhone.getText().toString()));
                // Start dialer
                startActivity(intent);
                break;
        }
    }
}
