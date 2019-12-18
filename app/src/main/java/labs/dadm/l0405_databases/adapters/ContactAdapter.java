/*
 * Copyright (c) 2018. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0405_databases.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import labs.dadm.l0405_databases.R;
import labs.dadm.l0405_databases.pojo.Contact;

/**
 * Custom adapter to associate the source data with Views within the ListView.
 */
public class ContactAdapter extends ArrayAdapter {

    // Hold reference to the layout
    private int layout;

    // Hold references to View elements
    private class ContactHolder {
        TextView tvName;
        TextView tvEmail;
        TextView tvPhone;
    }

    public ContactAdapter(@NonNull Context context, int resource, @NonNull List<Contact> objects) {
        super(context, resource, objects);
        this.layout = resource;
    }

    /*
        Creates and populates a View with the information from the required position of the data source.
    */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView;
        ContactHolder holder;

        // Reuse the View if it already exists
        if (result == null) {
            // Inflate the View to create it for the first time
            result = LayoutInflater.from(getContext()).inflate(layout, null);

            // Keep references for View elements in the layout
            holder = new ContactHolder();
            holder.tvName = result.findViewById(R.id.tvName);
            holder.tvEmail = result.findViewById(R.id.tvEmail);
            holder.tvPhone = result.findViewById(R.id.tvPhone);
            // Associate the ViewHolder to the View
            result.setTag(holder);
        }

        Contact contact = (Contact) getItem(position);
        // Retrieve the ViewHolder from the View
        holder = (ContactHolder) result.getTag();
        // Populate the View with information from the required position of the data source
        holder.tvName.setText(contact.getName());
        holder.tvEmail.setText(contact.getEmail());
        holder.tvPhone.setText(contact.getPhone());

        // Return the View
        return result;
    }

}
