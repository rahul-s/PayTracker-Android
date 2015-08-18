package com.creativecapsule.paytracker.Utility.AsyncTasks;

import android.os.AsyncTask;

import com.creativecapsule.paytracker.Managers.ParseDataManager;
import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 09/08/15.
 */
public class SaveExpenseTask extends AsyncTask <Void, Integer, Boolean> {

    private static final String DEBUG_TAG = "SaveOutingTask";
    private Expense expense;
    private TaskCompletedListener callbackListener;

    public SaveExpenseTask(Expense expense, TaskCompletedListener callbackListener) {
        this.expense = expense;
        this.callbackListener = callbackListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            boolean newExpense = true;
            if (expense.getParseId() != null && !expense.getParseId().equals("")) {
                newExpense = false;
            }

            // save expense
            ParseObject parseExpense = ParseDataManager.getSharedManager().getParseObject(expense);
            parseExpense.save();
            expense.setParseId(parseExpense.getObjectId());

            for (Person expensePerson : expense.getExpenseFor()) {
                saveExpenseForPerson(expense, expensePerson);
            }


            // save expense people
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (callbackListener != null) {
            callbackListener.completed(expense, aBoolean);
        }
    }

    private void saveExpenseForPerson(Expense expense, Person person) {
        ParseObject parseObject;
        ParseObject parseExpense = ParseDataManager.getSharedManager().getParseObject(expense);
        ParseObject parsePerson = ParseDataManager.getSharedManager().getParseObject(person);
        ParseQuery<ParseObject> expensePersonQuery = ParseQuery.getQuery(ParseDataManager.PARSE_TABLE_EXPENSE_PERSON);
        expensePersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_EXPENSE, parseExpense);
        expensePersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_PERSON, parsePerson);
        try {
            parseObject = expensePersonQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
            parseObject = new ParseObject(ParseDataManager.PARSE_TABLE_EXPENSE_PERSON);
            parseObject.put(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_EXPENSE, parseExpense);
            parseObject.put(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_PERSON, parsePerson);

        }
        try {
            parseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public interface TaskCompletedListener {
        void completed(Expense expense, boolean status);
    }
}
