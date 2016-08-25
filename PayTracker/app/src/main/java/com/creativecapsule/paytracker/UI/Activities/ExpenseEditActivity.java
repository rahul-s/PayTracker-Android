package com.creativecapsule.paytracker.UI.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.Utility.Common;

import java.util.ArrayList;

public class ExpenseEditActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    public static final String BUNDLE_KEY_OUTING_ID = "outing_id";
    public static final String BUNDLE_KEY_EXPENSE_ID = "expense_id";

    private Expense newExpense;
    private Outing outing;
    private Spinner byBuddySpinner;
    private ListView forBuddiesLV;
    private EditText amountTV, commentsTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int expenseId = getIntent().getIntExtra(BUNDLE_KEY_EXPENSE_ID, 0);
        if (expenseId != 0) {
            //Expense edit mode
            this.newExpense = ExpenseManager.getSharedInstance().getExpense(expenseId);
        }
        else {
            //New expense mode
            this.newExpense = new Expense(null, null, 0, null, "", "");
        }
        int outingId = getIntent().getIntExtra(BUNDLE_KEY_OUTING_ID, 0);
        this.outing = ExpenseManager.getSharedInstance().getOutingById(outingId);
        this.newExpense.setOuting(this.outing);

        //By spinner
        byBuddySpinner = (Spinner) findViewById(R.id.expense_by_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getOutingBuddiesNamesArray());
        byBuddySpinner.setAdapter(dataAdapter);
        byBuddySpinner.setOnItemSelectedListener(this);

        //Amount
        amountTV = (EditText) findViewById(R.id.expense_amount);

        //Comments
        commentsTV = (EditText) findViewById(R.id.expense_comments);

        //For Listview
        forBuddiesLV = (ListView) findViewById(R.id.expense_for_list);
        ArrayAdapter<String> buddiesAdapter = new ArrayAdapter<String>(ExpenseEditActivity.this, android.R.layout.simple_list_item_multiple_choice, getOutingBuddiesNamesArray());
        forBuddiesLV.setAdapter(buddiesAdapter);
        forBuddiesLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //buttons
        Button cancelBtn = (Button) findViewById(R.id.btn_expense_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button saveBtn = (Button) findViewById(R.id.btn_expense_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExpense();
            }
        });

        setupExpense();
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

    private String[] getOutingBuddiesNamesArray() {
        String[] names = new String[this.outing.getPersons().size()];
        int i=0 ;
        for (Person buddy : this.outing.getPersons()) {
            names[i] = buddy.getName();
            i++;
        }
        return names;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setupExpense() {
        if (this.newExpense.getIdentifier() == 0) {
            return;
        }
        amountTV.setText(this.newExpense.getAmount() + "");
        commentsTV.setText(this.newExpense.getNote());
        for (int i = 0 ; i < this.outing.getPersons().size() ; i++) {
            Person person = this.outing.getPersons().get(i);

            if(person.getIdentifier() == this.newExpense.getExpenseBy().getIdentifier()) {
                byBuddySpinner.setSelection(i);
            }

            for (int j = 0 ; j < this.newExpense.getExpenseFor().size() ; j++) {
                if (person.getIdentifier() == this.newExpense.getExpenseFor().get(j).getIdentifier()) {
                    forBuddiesLV.setItemChecked(i, true);
                }
            }
        }
    }

    private void saveExpense() {

        // by
        Person byExpenseBuddy = this.outing.getPersons().get(byBuddySpinner.getSelectedItemPosition());
        this.newExpense.setExpenseBy(byExpenseBuddy);

        // amount
        String amountStr = amountTV.getText().toString();
        if (!amountStr.matches("\\d+$")) {
            Toast.makeText(this, getResources().getString(R.string.alert_incorrect_amount), Toast.LENGTH_SHORT).show();
            return;
        }
        Integer amount = Integer.parseInt(amountStr);
        this.newExpense.setAmount(amount);

        // comments
        String comments = commentsTV.getText().toString();
        this.newExpense.setNote(comments);

        // for
        ArrayList<Person> selectedPeople = new ArrayList<Person>();
        for (int i=0 ; i<this.outing.getPersons().size() ; i++) {
            if (forBuddiesLV.getCheckedItemPositions().get(i)) {
                selectedPeople.add(this.outing.getPersons().get(i));
            }
        }
        this.newExpense.setExpenseFor(selectedPeople);
        if (selectedPeople.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.alert_select_people), Toast.LENGTH_SHORT).show();
            return;
        }

        // save
        Common.showLoadingDialog(this);
        ExpenseManager.getSharedInstance().saveExpense(this.newExpense, new ExpenseManager.ExpenseManagerListener() {
            @Override
            public void completed(boolean status) {
                Common.hideLoadingDialog();
                if (status) {
                    finish();
                } else {
                    Toast.makeText(ExpenseEditActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
