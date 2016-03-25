package com.creativecapsule.paytracker.UI.Activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.creativecapsule.paytracker.Models.Misc.ContactItem;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.ContactsAdapter;

import java.util.ArrayList;

public class AddBuddyActivity extends BaseActivity {

    public static final String DEBUG_TAG = "";

    private ArrayList<ContactItem> contacts;
    private ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buddy);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //fetch contacts and display in a list.

        fetchContacts();
        setupContactsListView();
    }

    private void setupContactsListView() {
        if (contactsAdapter == null) {
            contactsAdapter = new ContactsAdapter(contacts, this);
            ListView contactsListView = (ListView) findViewById(R.id.contacts_list);
            contactsListView.setAdapter(contactsAdapter);
        }
    }

    private void fetchContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        this.contacts = new ArrayList<>();

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                ContactItem contact = new ContactItem();
                contact.setContactName(name);

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.addContactNumber(phoneNo);
                    }
                    this.contacts.add(contact);
                    pCur.close();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_buddy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
