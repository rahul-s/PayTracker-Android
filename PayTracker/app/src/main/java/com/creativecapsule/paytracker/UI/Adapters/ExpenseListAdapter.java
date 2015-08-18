package com.creativecapsule.paytracker.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativecapsule.paytracker.Managers.ExpenseManager;
import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;

import java.util.ArrayList;

/**
 * Created by rahul on 18/08/15.
 */
public class ExpenseListAdapter extends BaseAdapter {
    private Context context;
    private Outing outing;
    private ArrayList<ExpenseItem> expenseItems;

    public ExpenseListAdapter(Context context, Outing outing) {
        this.context = context;
        this.outing = outing;
        setupExpenseItems();
    }

    @Override
    public int getCount() {
        if (expenseItems == null) {
            return 0;
        }
        else {
            return expenseItems.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ExpenseItemViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_expense_person, viewGroup, false);

            viewHolder = new ExpenseItemViewHolder();
            viewHolder.expensePersonName = (TextView) view.findViewById(R.id.expense_person_name);
            viewHolder.expensePersonCost = (TextView) view.findViewById(R.id.expense_person_cost);
            viewHolder.expensePersonSpent = (TextView) view.findViewById(R.id.expense_person_spent);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ExpenseItemViewHolder) view.getTag();
        }

        ExpenseItem expenseItem = this.expenseItems.get(i);
        viewHolder.expensePersonName.setText(expenseItem.person.getName());
        viewHolder.expensePersonCost.setText(expenseItem.outingCost+"");
        viewHolder.expensePersonSpent.setText(expenseItem.outingExpense+"");

        return view;
    }

    private class ExpenseItemViewHolder {
        TextView expensePersonName, expensePersonSpent, expensePersonCost, expensePersonConclusion;
    }

    private class ExpenseItem {
        Person person;
        int outingCost;
        int outingExpense;

        public ExpenseItem(Person person) {
            this.person = person;
            this.outingCost = 0;
            this.outingExpense = 0;
        }
    }

    private void setupExpenseItems() {
        this.expenseItems = new ArrayList<>();
        ArrayList<Expense> outingExpenses = ExpenseManager.getSharedInstance().getExpenses(this.outing);
        for (Person person : outing.getPersons()) {
            ExpenseItem item = new ExpenseItem(person);
            for (Expense expense : outingExpenses) {
                if (expense.isExpenseBy(person)) {
                    item.outingExpense += expense.getAmount();
                }

                if (expense.isExpenseFor(person)) {
                    item.outingCost += expense.getExpensePerPerson();
                }
            }
            this.expenseItems.add(item);
        }
    }
}
