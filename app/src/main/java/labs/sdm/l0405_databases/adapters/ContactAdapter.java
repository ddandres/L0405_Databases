package labs.sdm.l0405_databases.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import labs.sdm.l0405_databases.R;
import labs.sdm.l0405_databases.pojo.Contact;

/**
 * Custom adapter to associate the source data with Views within the ListView.
 */
public class ContactAdapter extends ArrayAdapter {

    // Hold reference to the context and layout
    private Context context;
    private int layout;

    // Hold references to View elements
    private class ContactHolder {
        TextView tvName;
        TextView tvEmail;
        TextView tvPhone;
    }

    public ContactAdapter(@NonNull Context context, int resource, @NonNull List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
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
            result = LayoutInflater.from(context).inflate(layout, null);

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

    /*
        Updates the information of a contact located at a given position.
     */
    public void update(Contact contact, int position) {
        // Get the contact located a the given position
        Contact replaceContact = (Contact) getItem(position);
        // Remove that contact
        remove(replaceContact);
        // Insert the new contact (the one with updated information) at that same position
        insert(contact, position);
    }

}
