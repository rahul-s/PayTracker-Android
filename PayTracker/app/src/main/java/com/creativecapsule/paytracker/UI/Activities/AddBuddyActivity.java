package com.creativecapsule.paytracker.UI.Activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.Models.Misc.ContactItem;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.ContactsAdapter;
import com.creativecapsule.paytracker.Utility.Common;

import java.util.ArrayList;

public class AddBuddyActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final String DEBUG_TAG = "";

    private ArrayList<ContactItem> contacts;
    private ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buddy);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fetchContacts();
        setupContactsListView();
        setupSearchFilter();
    }

    private void setupSearchFilter() {
        final EditText etContactsFilter = (EditText) findViewById(R.id.contact_search_field);
        etContactsFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = etContactsFilter.getText().toString();
                contactsAdapter.getFilter().filter(searchText.toLowerCase());
            }
        });
    }

    private void setupContactsListView() {
        if (contactsAdapter == null) {
            contactsAdapter = new ContactsAdapter(contacts, this);
            ListView contactsListView = (ListView) findViewById(R.id.contacts_list);
            contactsListView.setOnItemClickListener(this);
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

                boolean hasValidPhoneNumber = false;
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

                        phoneNo = Common.trimPhoneNumber(phoneNo);
                        if (Common.isValidPhoneNumber(phoneNo)) {
                            contact.addContactNumber(phoneNo);
                            hasValidPhoneNumber = true;
                        }
                    }
                    if (hasValidPhoneNumber) {
                        this.contacts.add(contact);
                    }
                    pCur.close();
                }
            }
        }
    }

    private void saveSelectedContacts() {
        Log.d(DEBUG_TAG, "selected numbers :" + contactsAdapter.getContactsSelected().size());

        Common.showLoadingDialog(this);
        ArrayList<Person> newBuddies = new ArrayList<>();
        for (int i=0 ; i<contactsAdapter.getContactsSelected().size() ; i++) {
            ContactItem contactItem = contactsAdapter.getContactsSelected().get(i);
            Person buddy = new Person(contactItem.getContactName(), contactItem.getSelectedContactNumber(), "");
            newBuddies.add(buddy);
        }
        UserAccountManager.getSharedManager().addPersons(newBuddies, new UserAccountManager.UserAccountManagerListener() {
            @Override
            public void completed(boolean status) {
                Common.hideLoadingDialog();
                finish();
            }
        });
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
        if (id == R.id.action_save) {
            saveSelectedContacts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        contactsAdapter.contactItemClicked(i);
    }
}
