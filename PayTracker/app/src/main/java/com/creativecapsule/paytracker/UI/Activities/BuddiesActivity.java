package com.creativecapsule.paytracker.UI.Activities;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.PersonsAdapter;
import com.creativecapsule.paytracker.Utility.Common;

import java.util.ArrayList;

public class BuddiesActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<Person> buddies;
    private View newBuddyDialogView;
    private Dialog newBuddyDialog;
    private PersonsAdapter personsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        buddies = UserAccountManager.getSharedManager().getPersons();
        personsAdapter = new PersonsAdapter(this);
        personsAdapter.setPersons(buddies);
        ListView buddiesListView = (ListView) findViewById(R.id.list_view_buddies);
        buddiesListView.setAdapter(personsAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buddies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_add_buddy) {
            //Add new buddy.
            showNewBuddyDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_buddy_save:
                saveBuddy();
                break;
            case R.id.new_buddy_cancel:
                cancelNewBuddy();
                break;
        }
    }

    private void showNewBuddyDialog() {
        newBuddyDialog = new Dialog(this);
        newBuddyDialog.setCancelable(false);
        newBuddyDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        newBuddyDialogView = inflater.inflate(R.layout.dialog_new_buddy, null, false);

        Button saveBuddyBtn = (Button) newBuddyDialogView.findViewById(R.id.new_buddy_save);
        saveBuddyBtn.setOnClickListener(this);

        Button cancelBuddyBtn = (Button) newBuddyDialogView.findViewById(R.id.new_buddy_cancel);
        cancelBuddyBtn.setOnClickListener(this);

        newBuddyDialog.setContentView(newBuddyDialogView);
        newBuddyDialog.show();
    }

    public void saveBuddy() {
        EditText etEmail = (EditText) newBuddyDialogView.findViewById(R.id.new_buddy_email);
        EditText etName = (EditText) newBuddyDialogView.findViewById(R.id.new_buddy_name);
        EditText etNickname = (EditText) newBuddyDialogView.findViewById(R.id.new_buddy_nick_name);

        String email = etEmail.getText().toString();
        if (!Common.isValidEmail(email)) {
            Toast.makeText(BuddiesActivity.this, getResources().getString(R.string.alert_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        Person newBuddy = new Person(etName.getText().toString(), email, etNickname.getText().toString());
        UserAccountManager.getSharedManager().savePerson(newBuddy);
        newBuddyDialog.dismiss();

        reloadBuddyList();
    }

    private void cancelNewBuddy() {
        newBuddyDialog.dismiss();
    }

    private void reloadBuddyList() {
        buddies = UserAccountManager.getSharedManager().getPersons();
        personsAdapter.setPersons(buddies);
        personsAdapter.notifyDataSetChanged();
    }
}
