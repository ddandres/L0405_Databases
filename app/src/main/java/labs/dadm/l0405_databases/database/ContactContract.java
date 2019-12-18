/*
 * Copyright (c) 2018. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0405_databases.database;

import android.provider.BaseColumns;

/*
    Contracts define constants for names of URIs, tables and columns.
 */
class ContactContract {

    // Prevents anyone from instantiating this class
    private ContactContract() {
    }

    // Contents for contacts table
    // By implementing BaseColumns it inherits a primary key called _ID
    static class ContactEntry implements BaseColumns {
        static final String TABLE_NAME = "contacts_table";
        static final String COLUMN_NAME_ID = "_ID";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_PHONE = "phone";
    }
}
