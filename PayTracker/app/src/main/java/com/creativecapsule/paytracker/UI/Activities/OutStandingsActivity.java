package com.creativecapsule.paytracker.UI.Activities;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.Adapters.OutStandingsAdapter;
import com.creativecapsule.paytracker.UI.CustomViews.SectionHeaderListView.SectionHeaderListView;

public class OutStandingsActivity extends BaseActivity {

    public static final String BUNDLE_KEY_OUTING_ID = "outing_id";

    private Outing outing;
    private OutStandingsAdapter outStandingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oustandings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int outingId = getIntent().getIntExtra(BUNDLE_KEY_OUTING_ID, 0);
        this.outing = ExpenseManager.getSharedInstance().getOutingById(outingId);

        SectionHeaderListView outstandingListView = (SectionHeaderListView) findViewById(R.id.list_view_outstandings);
        this.outStandingsAdapter = new OutStandingsAdapter(this, this.outing);
        outstandingListView.setAdapter(this.outStandingsAdapter);
        outstandingListView.setHeaderFreeze(true);

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
