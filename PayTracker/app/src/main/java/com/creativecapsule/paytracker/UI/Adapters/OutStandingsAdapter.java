package com.creativecapsule.paytracker.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.UI.CustomViews.SectionHeaderListView.SectionHeaderBaseAdapter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rahul on 04/08/15.
 */
public class OutStandingsAdapter extends SectionHeaderBaseAdapter {

    private Context context;
    private Outing outing;
    private ArrayList<OutStanding> outStandings;
    private boolean isOutStandingAvailable;

    public OutStandingsAdapter(Context context, Outing outing) {
        this.context = context;
        this.outing = outing;
        this.isOutStandingAvailable = false;
        setupOutStandings();
    }

    @Override
    public int numberOfSections() {
        if (!isOutStandingAvailable) {
            return 0;
        }
        return this.outStandings.size();
    }

    @Override
    public int numberOfRowsInSection(int section) {
        if (!isOutStandingAvailable) {
            return 0;
        }
        OutStanding outStanding = outStandings.get(section);
        return outStanding.outstandingPayments.size();
    }

    @Override
    public int getHeightForHeaderView() {
        return 0;
    }

    @Override
    public View getSectionHeaderView(int section, View view, ViewGroup parent) {
        OutStandingSectionHolder sectionHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_outstanding_header, parent, false);
            sectionHolder = new OutStandingSectionHolder();

            sectionHolder.byLabel = (TextView) view.findViewById(R.id.outstanding_by_label);
            view.setTag(sectionHolder);
        }
        else {
            sectionHolder = (OutStandingSectionHolder) view.getTag();
        }
        OutStanding outStanding = outStandings.get(section);
        sectionHolder.byLabel.setText(outStanding.person.getName());
        return view;
    }

    @Override
    public View getRowView(int section, int row, View view, ViewGroup parent) {
        OutStandingRowHolder rowHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_outstanding_row, parent, false);
            rowHolder = new OutStandingRowHolder();

            rowHolder.toLabel = (TextView) view.findViewById(R.id.outstanding_to_label);
            rowHolder.amountLabel = (TextView) view.findViewById(R.id.outstanding_amount_label);
            view.setTag(rowHolder);
        }
        else {
            rowHolder = (OutStandingRowHolder) view.getTag();
        }
        OutStanding outStanding = outStandings.get(section);
        Pay payment = outStanding.outstandingPayments.get(row);
        rowHolder.toLabel.setText(payment.payTo.getName());
        rowHolder.amountLabel.setText(payment.amount + "");
        return view;
    }

    @Override
    public int getRowViewType(int section, int row) {
        return 1;
    }

    @Override
    public int getRowViewTypeCount() {
        return 1;
    }

    @Override
    public Object getRowItem(int section, int row) {
        return null;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public class Pay {
        Person payTo;
        int amount;

        public Pay(Person payTo, int amount) {
            this.payTo = payTo;
            this.amount = amount;
        }
    }

    public class OutStanding {
        Person person;
        ArrayList<Pay> outstandingPayments;

        public OutStanding(Person person) {
            this.person = person;
            outstandingPayments = new ArrayList<Pay>();
        }

        public void addPayment(Pay payment) {
            Pay existingPayment = getOutstandingPayment(payment.payTo);
            if (existingPayment == null) {
                // create a new pay for this person
                outstandingPayments.add(payment);
            }
            else {
                // add the amount to the persons existing payment.
                existingPayment.amount += payment.amount;
            }
        }

        public void removePayment(Pay payment) {
            outstandingPayments.remove(payment);
        }

        /**
         * return the outstanding payment for this given person if any, otherwise null.
         * @param person
         * @return
         */
        public Pay getOutstandingPayment(Person person) {
            if (this.outstandingPayments == null || this.outstandingPayments.size() == 0) {
                return null;
            }

            for (Pay payment : this.outstandingPayments) {
                if (payment.payTo.getIdentifier() == person.getIdentifier()) {
                    return payment;
                }
            }
            return null;
        }
    }

    public class OutStandingSectionHolder {
        TextView byLabel;
    }

    public class OutStandingRowHolder {
        TextView toLabel, amountLabel;
    }

    private void setupOutStandings() {
        this.outStandings = new ArrayList<OutStanding>();
        ArrayList<Expense> outingExpenses = ExpenseManager.getSharedInstance().getExpenses(this.outing);
        for (Person person : this.outing.getPersons()) {
            OutStanding outStanding = new OutStanding(person);

            for (Expense expense : outingExpenses) {
                if (expense.isExpenseBy(person)) {
                    // Skip if the expense was done by this person
                    continue;
                }

                if (expense.isExpenseFor(person)) {
                    // Expense was for this person
                    Pay payment = new Pay(expense.getExpenseBy(), expense.getExpensePerPerson());
                    outStanding.addPayment(payment);
                }
            }

            if (outStanding.outstandingPayments.size() > 0) {
                this.outStandings.add(outStanding);
            }
        }

        optimizeOutStandings();

        if (this.outStandings != null && this.outStandings.size() > 0) {
            this.isOutStandingAvailable = true;
        }
    }

    /**
     * This method removes any cross payments if existing and replaces with a resulting payment from the difference.
     */
    private void optimizeOutStandings() {

        // Clubbing together payments to each others
        for (OutStanding outStandingOne : this.outStandings) {
            for (OutStanding outStandingTwo : this.outStandings) {
                if (outStandingOne.person.getIdentifier() == outStandingTwo.person.getIdentifier()) {
                    //skip if same user
                    continue;
                }

                Pay paymentOne = outStandingOne.getOutstandingPayment(outStandingTwo.person);
                Pay paymentTwo = outStandingTwo.getOutstandingPayment(outStandingOne.person);

                if (paymentOne != null && paymentTwo != null) {
                    //Cross payment exists. needs to be optimised.
                    if (paymentOne.amount == paymentTwo.amount) {
                        // both outstanding payments are equal. hence both can removed.
                        outStandingOne.removePayment(paymentOne);
                        outStandingTwo.removePayment(paymentTwo);
                    }
                    else if (paymentOne.amount > paymentTwo.amount) {
                        // payment one is greater, hence payment two can be removed.
                        paymentOne.amount = paymentOne.amount - paymentTwo.amount;
                        outStandingTwo.removePayment(paymentTwo);
                    }
                    else if (paymentOne.amount < paymentTwo.amount) {
                        // payment two is greater, hence payment one can be removed.
                        paymentTwo.amount = paymentTwo.amount - paymentOne.amount;
                        outStandingOne.removePayment(paymentOne);
                    }
                }

            }
        }

        // remove any out standings if no payments
        for (Iterator<OutStanding> iterator = this.outStandings.iterator(); iterator.hasNext();) {
            OutStanding outStanding = iterator.next();
            if (outStanding.outstandingPayments == null || outStanding.outstandingPayments.size() == 0) {
                iterator.remove();
            }
        }
    }
}
