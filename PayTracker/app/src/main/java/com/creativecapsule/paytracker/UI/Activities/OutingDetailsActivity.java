package com.creativecapsule.paytracker.UI.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.ExpenseAdapter;
import com.creativecapsule.paytracker.UI.Adapters.PersonsAdapter;
import com.creativecapsule.paytracker.Utility.Common;

import java.util.ArrayList;

public class OutingDetailsActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ExpenseAdapter.ExpenseAdapterListener {

    private static final int OUTING_TAB_EXPENSES = 1;
    private static final int OUTING_TAB_BUDDIES = 2;

    public static final String OUTING_BUNDLE_KEY = "outing_id";

    private Outing outing;
    private ListView outingDetailsListView;
    private int activeTab;
    private PersonsAdapter outingBuddiesAdapter;
    private ExpenseAdapter outingExpenseAdapter;
    private ArrayList<Person> outingBuddies, otherBuddies, shareBuddies;
    private ArrayList<Expense> outingExpenses;
    private Dialog addBuddiesDialog;
    private View addBuddiesDialogView;
    private Dialog shareOutingDialog;
    private View shareOutingDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outing_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int outingId = getIntent().getIntExtra(OUTING_BUNDLE_KEY, 0);
        this.outing = ExpenseManager.getSharedInstance().getOutingById(outingId);

        initExpenseList();
        initBuddiesList();
        initOtherBuddies();

        outingDetailsListView = (ListView) findViewById(R.id.outing_detail_list_view);

        Button tabExpenseBtn = (Button) findViewById(R.id.tab_btn_expenses);
        tabExpenseBtn.setOnClickListener(this);
        Button tabBuddiesBtn = (Button) findViewById(R.id.tab_btn_buddies);
        tabBuddiesBtn.setOnClickListener(this);

        showTab(OUTING_TAB_EXPENSES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initExpenseList();
        initBuddiesList();
        initOtherBuddies();
        showTab(this.activeTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_outing_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_add) {
            if (this.activeTab == OUTING_TAB_EXPENSES) {
                addExpense();
            }
            if (this.activeTab == OUTING_TAB_BUDDIES) {
                addBuddy();
            }
            return true;
        }
        if (id == R.id.action_show_expenses) {
            showExpenses();
            return true;
        }
        if (id == R.id.action_show_outstandings) {
            showOutstandings();
            return true;
        }
        if (id == R.id.action_share_outing) {
            showShareOutingDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_btn_expenses:
                showTab(OUTING_TAB_EXPENSES);
                break;
            case R.id.tab_btn_buddies:
                showTab(OUTING_TAB_BUDDIES);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    //region Expense Adapter Listener

    @Override
    public void editExpense(int index) {
        Expense expense = this.outingExpenses.get(index);
        Intent newExpenseIntent = new Intent(this, ExpenseActivity.class);
        newExpenseIntent.putExtra(ExpenseActivity.BUNDLE_KEY_OUTING_ID, this.outing.getIdentifier());
        newExpenseIntent.putExtra(ExpenseActivity.BUNDLE_KEY_EXPENSE_ID, expense.getIdentifier());
        startActivity(newExpenseIntent);
    }

    @Override
    public void deleteExpense(final int index) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setMessage(R.string.dialog_confirm_delete_expense)
                .setPositiveButton(R.string.dialog_confirm_delete_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: delete expense.
                        Expense expense = outingExpenses.get(index);
                        ExpenseManager.getSharedInstance().deleteExpense(expense.getIdentifier(), new ExpenseManager.ExpenseManagerListener() {
                            @Override
                            public void completed(boolean status) {
                                if (status) {
                                    initExpenseList();
                                    initBuddiesList();
                                    showTab(activeTab);
                                }
                                else {
                                    Toast.makeText(OutingDetailsActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.dialog_confirm_delete_no, null)
                .show();

    }

    //endregion

    //region Tab Switching
    private void showTab(int tab) {
        switch (tab) {
            case OUTING_TAB_EXPENSES:
                switchToExpensesTab();
                break;
            case OUTING_TAB_BUDDIES:
                switchToBuddiesTab();
                break;
        }

        this.activeTab = tab;
    }

    private void switchToExpensesTab() {
        outingDetailsListView.setAdapter(outingExpenseAdapter);
        outingExpenseAdapter.notifyDataSetChanged();
    }

    private void switchToBuddiesTab() {
        outingDetailsListView.setAdapter(outingBuddiesAdapter);
        outingBuddiesAdapter.notifyDataSetChanged();
    }

    private void initExpenseList() {
        outingExpenseAdapter = new ExpenseAdapter(this);
        outingExpenses = ExpenseManager.getSharedInstance().getExpenses(this.outing);
        outingExpenseAdapter.setExpenses(outingExpenses);
        outingExpenseAdapter.setAdapterListener(this);
    }

    private void initBuddiesList() {
        outingBuddiesAdapter = new PersonsAdapter(this);
        outingBuddies = this.outing.getPersons();
        outingBuddiesAdapter.setPersons(outingBuddies);
    }

    private void initOtherBuddies() {
        otherBuddies = new ArrayList<>();
        shareBuddies = new ArrayList<>();
        Person mySelf = UserAccountManager.getSharedManager().getLoggedInPerson();
        ArrayList<Person> allBuddies = UserAccountManager.getSharedManager().getPersons();
        for (Person buddy : allBuddies) {
            if (!this.outing.isPersonIncluded(buddy)) {
                otherBuddies.add(buddy);
            }
            if (!(buddy.getIdentifier() == mySelf.getIdentifier())) {
                shareBuddies.add(buddy);
            }
        }
    }
    //endregion

    private void addBuddy() {
        if (this.otherBuddies == null || this.otherBuddies.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.alert_no_more_buddies), Toast.LENGTH_SHORT).show();
            return;
        }
        addBuddiesDialog = new Dialog(this);
        addBuddiesDialog.setCancelable(false);
        addBuddiesDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        addBuddiesDialogView = inflater.inflate(R.layout.dialog_select_people, null, false);

        ListView buddiesListView = (ListView) addBuddiesDialogView.findViewById(R.id.people_select_list);
        ArrayAdapter<String> buddiesAdapter = new ArrayAdapter<String>(OutingDetailsActivity.this, android.R.layout.simple_list_item_multiple_choice, getOtherBuddiesNamesArray());
        buddiesListView.setAdapter(buddiesAdapter);
        buddiesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button saveOutingBtn = (Button) addBuddiesDialogView.findViewById(R.id.new_outing_2_save);
        saveOutingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBuddySave();
            }
        });

        Button cancelOutingBtn = (Button) addBuddiesDialogView.findViewById(R.id.new_outing_2_cancel);
        cancelOutingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBuddiesDialog.dismiss();
            }
        });

        addBuddiesDialog.setContentView(addBuddiesDialogView);
        addBuddiesDialog.show();
    }

    private void addBuddySave() {
        ArrayList<Person> selectedPeople = new ArrayList<>();
        ListView buddiesListView = (ListView) addBuddiesDialogView.findViewById(R.id.people_select_list);
        for (int i=0 ; i<this.otherBuddies.size() ; i++) {
            if (buddiesListView.getCheckedItemPositions().get(i)) {
                selectedPeople.add(this.otherBuddies.get(i));
            }
        }
        this.outing.addPersons(selectedPeople);
        Common.showLoadingDialog(this);
        ExpenseManager.getSharedInstance().saveOuting(this.outing, new ExpenseManager.ExpenseManagerListener() {
            @Override
            public void completed(boolean status) {
                Common.hideLoadingDialog();
                if (status) {
                    addBuddiesDialog.dismiss();
                    initBuddiesList();
                    initOtherBuddies();
                    showTab(activeTab);
                } else {
                    Toast.makeText(OutingDetailsActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addExpense() {
        Intent newExpenseIntent = new Intent(this, ExpenseActivity.class);
        newExpenseIntent.putExtra(ExpenseActivity.BUNDLE_KEY_OUTING_ID, this.outing.getIdentifier());
        startActivity(newExpenseIntent);
    }

    private void showExpenses() {

    }

    private void showOutstandings() {
        Intent outstandingsIntent = new Intent(this, OutStandingsActivity.class);
        outstandingsIntent.putExtra(ExpenseActivity.BUNDLE_KEY_OUTING_ID, this.outing.getIdentifier());
        startActivity(outstandingsIntent);
    }

    private void showShareOutingDialog() {
        shareOutingDialog = new Dialog(this);
        shareOutingDialog.setCancelable(false);
        shareOutingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        shareOutingDialogView = inflater.inflate(R.layout.dialog_select_people, null, false);

        ListView buddiesListView = (ListView) shareOutingDialogView.findViewById(R.id.people_select_list);
        ArrayAdapter<String> buddiesAdapter = new ArrayAdapter<String>(OutingDetailsActivity.this, android.R.layout.simple_list_item_multiple_choice, getShareBuddiesNamesArray());
        buddiesListView.setAdapter(buddiesAdapter);
        buddiesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Button saveOutingBtn = (Button) shareOutingDialogView.findViewById(R.id.new_outing_2_save);
        saveOutingBtn.setText(getResources().getString(R.string.label_share));
        saveOutingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOuting();
            }
        });

        Button cancelOutingBtn = (Button) shareOutingDialogView.findViewById(R.id.new_outing_2_cancel);
        cancelOutingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOutingDialog.dismiss();
            }
        });

        shareOutingDialog.setContentView(shareOutingDialogView);
        shareOutingDialog.show();
    }

    private void shareOuting() {
        Person selectedPerson = null;
        ListView buddiesListView = (ListView) shareOutingDialogView.findViewById(R.id.people_select_list);
        for (int i=0 ; i<this.shareBuddies.size() ; i++) {
            if (buddiesListView.getCheckedItemPositions().get(i)) {
                selectedPerson = this.shareBuddies.get(i);
            }
        }

        if (selectedPerson == null) {
            //No selection
            Toast.makeText(OutingDetailsActivity.this, getResources().getString(R.string.alert_select_people_share), Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Common.showLoadingDialog(this);
            ExpenseManager.getSharedInstance().shareOuting(this.outing, selectedPerson, new ExpenseManager.ExpenseManagerListener() {
                @Override
                public void completed(boolean status) {
                    Common.hideLoadingDialog();
                    if (status) {
                        Toast.makeText(OutingDetailsActivity.this, getResources().getString(R.string.alert_shared_success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(OutingDetailsActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                    }
                    shareOutingDialog.dismiss();
                }
            });
        }
    }

    private String[] getOtherBuddiesNamesArray() {
        String[] names = new String[otherBuddies.size()];
        int i=0 ;
        for (Person buddy : otherBuddies) {
            names[i] = buddy.getName();
            i++;
        }
        return names;
    }

    private String[] getShareBuddiesNamesArray() {
        String[] names = new String[shareBuddies.size()];
        int i=0 ;
        for (Person buddy : shareBuddies) {
            names[i] = buddy.getName();
            i++;
        }
        return names;
    }
}
