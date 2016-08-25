package com.creativecapsule.paytracker.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.R;

import java.util.ArrayList;

/**
 * Created by rahul on 05/07/15.
 */
public class ExpenseAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Expense> expenses;
    private ExpenseAdapterListener adapterListener;

    public ExpenseAdapter(Context context) {
        this.context = context;
        this.expenses = new ArrayList<Expense>();
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public void setAdapterListener(ExpenseAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    @Override
    public int getCount() {
        return this.expenses.size();
    }

    @Override
    public Object getItem(int i) {
        return this.expenses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ExpenseViewHolder expenseViewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_expense, viewGroup, false);
            expenseViewHolder = new ExpenseViewHolder();

            expenseViewHolder.tvExpenseBy = (TextView) view.findViewById(R.id.expense_by_name);
            expenseViewHolder.tvExpenseFor = (TextView) view.findViewById(R.id.expense_for_names);
            expenseViewHolder.tvExpenseComments = (TextView) view.findViewById(R.id.expense_comments);
            expenseViewHolder.tvExpenseAmount = (TextView) view.findViewById(R.id.expense_amount);

            expenseViewHolder.btnExpenseEdit = (Button) view.findViewById(R.id.expense_edit_btn);
            expenseViewHolder.btnExpenseDelete = (Button) view.findViewById(R.id.expense_delete_btn);

            view.setTag(expenseViewHolder);
        }
        else {
            expenseViewHolder = (ExpenseViewHolder) view.getTag();
        }

        Expense expense = this.expenses.get(index);
        expenseViewHolder.tvExpenseBy.setText(expense.getExpenseBy().getName());
        expenseViewHolder.tvExpenseAmount.setText("Rs. " +expense.getAmount());
        expenseViewHolder.tvExpenseComments.setText(expense.getNote());
        expenseViewHolder.tvExpenseFor.setText("For " + expense.getBuddiesText());
        expenseViewHolder.btnExpenseEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapterListener != null) {
                    adapterListener.editExpense(index);
                }
            }
        });
        expenseViewHolder.btnExpenseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapterListener != null) {
                    adapterListener.deleteExpense(index);
                }
            }
        });
        return view;
    }

    public interface ExpenseAdapterListener {
        void editExpense(int index);
        void deleteExpense(int index);
    }

    public class ExpenseViewHolder {
        public TextView tvExpenseBy, tvExpenseFor, tvExpenseComments, tvExpenseAmount;
        public Button btnExpenseEdit, btnExpenseDelete;
    }
}
