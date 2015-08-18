package com.creativecapsule.paytracker.UI.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.ExpenseListAdapter;

public class ExpenseListActivity extends BaseActivity {


    public static final String BUNDLE_KEY_OUTING_ID = "outing_id";

    private Outing outing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int outingId = getIntent().getIntExtra(BUNDLE_KEY_OUTING_ID, 0);
        this.outing = ExpenseManager.getSharedInstance().getOutingById(outingId);

        ListView expenseListView = (ListView) findViewById(R.id.list_view_expenses);
        ExpenseListAdapter listAdapter = new ExpenseListAdapter(this, this.outing);
        expenseListView.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
