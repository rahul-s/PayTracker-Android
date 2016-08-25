package com.creativecapsule.paytracker.UI.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.OutingsAdapter;
import com.creativecapsule.paytracker.Utility.Common;

import java.util.ArrayList;


public class HomeActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ArrayList<Outing> outings;
    private ArrayList<Person> buddies;
    private Outing newOuting;
    private Dialog newOutingDialog1, newOutingDialog2;
    private View newOutingDialog1View, newOutingDialog2View;
    private OutingsAdapter outingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu_home));

        outings = ExpenseManager.getSharedInstance().getOutings();
        buddies = UserAccountManager.getSharedManager().getPersons();
        outingsAdapter = new OutingsAdapter(this);
        outingsAdapter.setOutings(outings);
        ListView outingsListView = (ListView) findViewById(R.id.list_view_outings);
        outingsListView.setAdapter(outingsAdapter);
        outingsListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buddies = UserAccountManager.getSharedManager().getPersons();
        reloadOutingsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_buddies) {
            //Open Buddies list
            openBuddiesList();
            return true;
        }
        if (id == R.id.action_add_outing) {
            //Add Outings
            showNewOutingDialog();
            return true;
        }
        if (id == R.id.action_reload) {
            //Add Outings
            reloadUserData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.list_view_outings:
                //TODO: open outings details page
                Outing outing = this.outings.get(i);
                openOutingDetails(outing);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_outing_1_save:
                newOutingStep1Completed();
                break;
            case R.id.new_outing_1_cancel:
                cancelNewOuting();
                break;
            case R.id.new_outing_2_save:
                newOutingStep2Completed();
                break;
            case R.id.new_outing_2_cancel:
                cancelNewOuting();
                break;
        }
    }

    private void openBuddiesList() {
        Intent buddiesIntent = new Intent(this, BuddiesActivity.class);
        startActivity(buddiesIntent);
    }

    private void openOutingDetails(Outing outing) {
        Intent outingIntent = new Intent(this, OutingDetailsActivity.class);
        outingIntent.putExtra(OutingDetailsActivity.OUTING_BUNDLE_KEY, outing.getIdentifier());
        startActivity(outingIntent);
    }

    // New Outing Step 1
    private void showNewOutingDialog() {
        newOutingDialog1 = new Dialog(this);
        newOutingDialog1.setCancelable(false);
        newOutingDialog1.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        newOutingDialog1View = inflater.inflate(R.layout.dialog_new_outing, null, false);

        Button saveOutingBtn = (Button) newOutingDialog1View.findViewById(R.id.new_outing_1_save);
        saveOutingBtn.setOnClickListener(this);

        Button cancelOutingBtn = (Button) newOutingDialog1View.findViewById(R.id.new_outing_1_cancel);
        cancelOutingBtn.setOnClickListener(this);

        newOutingDialog1.setContentView(newOutingDialog1View);
        newOutingDialog1.show();
    }

    private void newOutingStep1Completed() {
        EditText tvOutingName = (EditText) newOutingDialog1View.findViewById(R.id.new_outing_title);

        if (tvOutingName.getText().toString().equals("")){
            Toast.makeText(this, getResources().getString(R.string.alert_name_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            newOutingDialog1.dismiss();
            newOuting = new Outing(tvOutingName.getText().toString(), null);
            showNewOutingBuddiesDialog();
        }
    }

    // New Outing Step 2
    private void showNewOutingBuddiesDialog() {
        newOutingDialog2 = new Dialog(this);
        newOutingDialog2.setCancelable(false);
        newOutingDialog2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        newOutingDialog2View = inflater.inflate(R.layout.dialog_select_people, null, false);

        ListView buddiesListView = (ListView) newOutingDialog2View.findViewById(R.id.people_select_list);
        ArrayAdapter<String> buddiesAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_list_item_multiple_choice, getBuddiesNamesArray());
        buddiesListView.setAdapter(buddiesAdapter);
        buddiesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button saveOutingBtn = (Button) newOutingDialog2View.findViewById(R.id.new_outing_2_save);
        saveOutingBtn.setOnClickListener(this);

        Button cancelOutingBtn = (Button) newOutingDialog2View.findViewById(R.id.new_outing_2_cancel);
        cancelOutingBtn.setOnClickListener(this);

        newOutingDialog2.setContentView(newOutingDialog2View);
        newOutingDialog2.show();
    }

    private void newOutingStep2Completed() {
        ArrayList<Person> selectedPeople = new ArrayList<Person>();
        ListView buddiesListView = (ListView) newOutingDialog2View.findViewById(R.id.people_select_list);
        for (int i=0 ; i<this.buddies.size() ; i++) {
            if (buddiesListView.getCheckedItemPositions().get(i)) {
                selectedPeople.add(this.buddies.get(i));
            }
        }
        newOuting.setPersons(selectedPeople);
        Common.showLoadingDialog(this);
        ExpenseManager.getSharedInstance().saveOuting(newOuting, new ExpenseManager.ExpenseManagerListener() {
            @Override
            public void completed(boolean status) {
                Common.hideLoadingDialog();
                if (status) {
                    newOutingDialog2.dismiss();
                    reloadOutingsList();
                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cancelNewOuting() {
        if (newOutingDialog1 != null) {
            newOutingDialog1.dismiss();
        }
        if (newOutingDialog2 != null) {
            newOutingDialog2.dismiss();
        }
    }

    private void reloadOutingsList() {
        outings = ExpenseManager.getSharedInstance().getOutings();
        outingsAdapter.setOutings(outings);
        outingsAdapter.notifyDataSetChanged();
    }

    private void reloadUserData() {
        Common.showLoadingDialog(this);
        UserAccountManager.getSharedManager().downloadUserData(new UserAccountManager.UserAccountManagerListener() {
            @Override
            public void completed(boolean status) {
                Common.hideLoadingDialog();
                if (status) {
                    reloadOutingsList();
                }
                else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String[] getBuddiesNamesArray() {
        String[] names = new String[buddies.size()];
        int i=0 ;
        for (Person buddy : buddies) {
            names[i] = buddy.getName();
            i++;
        }
        return names;
    }
}
