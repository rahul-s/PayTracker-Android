package com.creativecapsule.paytracker.Utility.AsyncTasks;

import android.os.AsyncTask;

import com.creativecapsule.paytracker.Managers.ParseDataManager;
import com.creativecapsule.paytracker.Models.Common.BaseModel;
import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 10/08/15.
 */
public class DownloadDataTask extends AsyncTask <Void, Integer, Void> {

    private Person myself;
    private DownloadTaskCompleted callbackListener;

    private boolean errorOccured;

    public DownloadDataTask(Person myself, DownloadTaskCompleted callbackListener) {
        this.myself = myself;
        this.callbackListener = callbackListener;
        this.errorOccured = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            // TODO: 1. fetch buddy of myself
            ParseQuery<ParseObject> buddiesQuery = new ParseQuery<ParseObject>(ParseDataManager.PARSE_TABLE_BUDDIES);
            ParseObject parseSelf = ParseDataManager.getSharedManager().getParseObject(myself);
            buddiesQuery.whereEqualTo(ParseDataManager.PARSE_KEY_BUDDIES_BUDDY_ONE, parseSelf);
            buddiesQuery.include(ParseDataManager.PARSE_KEY_BUDDIES_BUDDY_TWO);
            List<ParseObject> parseBuddies = buddiesQuery.find();
            for (ParseObject parseBuddy : parseBuddies) {
                ParseObject parseBuddyPerson = parseBuddy.getParseObject(ParseDataManager.PARSE_KEY_BUDDIES_BUDDY_TWO);
                Person buddy = ParseDataManager.getSharedManager().getPerson(parseBuddyPerson);
                if (callbackListener != null) {
                    callbackListener.downloadedBuddy(buddy);
                }
            }

            // TODO: 2. fetch outing objects owned by myself
            ParseQuery<ParseObject> outingQuery = new ParseQuery<ParseObject>(ParseDataManager.PARSE_TABLE_OUTING_OWNER);
            outingQuery.whereEqualTo(ParseDataManager.PARSE_KEY_OUTING_OWNER_OWNER, parseSelf);
            outingQuery.include(ParseDataManager.PARSE_KEY_OUTING_OWNER_OUTING);
            List<ParseObject> parseOutings = outingQuery.find();
            for (ParseObject parseOuting : parseOutings) {
                // TODO: 3.1. For each outing download outing persons and add to buddy
                ParseQuery<ParseObject> outingPersonQuery = new ParseQuery<ParseObject>(ParseDataManager.PARSE_TABLE_OUTING_PERSON);
                ParseObject parseOutingObject = parseOuting.getParseObject(ParseDataManager.PARSE_KEY_OUTING_OWNER_OUTING);
                outingPersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_OUTING_PERSON_OUTING, parseOutingObject);
                outingPersonQuery.include(ParseDataManager.PARSE_KEY_OUTING_PERSON_PERSON);
                List<ParseObject> parseOutingPersons = outingPersonQuery.find();
                ArrayList<ParseObject> parseOutingPersonObjects = new ArrayList<>();
                for (ParseObject parseOutingPerson : parseOutingPersons) {
                    ParseObject parseOutingPersonObj = parseOutingPerson.getParseObject(ParseDataManager.PARSE_KEY_OUTING_PERSON_PERSON);
                    parseOutingPersonObjects.add(parseOutingPersonObj);
                    Person buddy = ParseDataManager.getSharedManager().getPerson(parseOutingPersonObj);
                    if (callbackListener != null) {
                        callbackListener.downloadedBuddy(buddy);
                    }
                }

                Outing outing = ParseDataManager.getSharedManager().getOuting(parseOutingObject, parseOutingPersonObjects);
                if (callbackListener != null) {
                    callbackListener.downloadedOuting(outing);
                }

                // TODO: 3.2. For each outing download expense and expense person. Include expense person.
                ParseQuery<ParseObject> expenseQuery = new ParseQuery<ParseObject>(ParseDataManager.PARSE_TABLE_EXPENSE);
                expenseQuery.whereEqualTo(ParseDataManager.PARSE_KEY_EXPENSE_OUTING, parseOutingObject);
                expenseQuery.include(ParseDataManager.PARSE_KEY_EXPENSE_EXPENSE_BY);
                List<ParseObject> parseExpenses = expenseQuery.find();
                for (ParseObject parseExpense : parseExpenses) {
                    // TODO: 3.2.1. For each expense fetch the expense for persons.
                    ParseQuery<ParseObject> expensePersonQuery = new ParseQuery<ParseObject>(ParseDataManager.PARSE_TABLE_EXPENSE_PERSON);
                    expensePersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_EXPENSE, parseExpense);
                    expensePersonQuery.include(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_PERSON);
                    List<ParseObject> parseExpensePersons = expensePersonQuery.find();
                    ArrayList<ParseObject> parseExpensePersonObjects = new ArrayList<>();
                    for (ParseObject parseExpensePerson : parseExpensePersons) {
                        ParseObject parseExpensePersonObject = parseExpensePerson.getParseObject(ParseDataManager.PARSE_KEY_EXPENSE_PERSON_PERSON);
                        parseExpensePersonObjects.add(parseExpensePersonObject);
                        Person expenseBuddy = ParseDataManager.getSharedManager().getPerson(parseExpensePersonObject);
                        if (callbackListener != null) {
                            callbackListener.downloadedBuddy(expenseBuddy);
                        }
                    }

                    Expense expense = ParseDataManager.getSharedManager().getExpense(parseExpense, parseExpensePersonObjects, outing);
                    if (callbackListener != null) {
                        callbackListener.downloadedExpense(expense);
                    }
                }
            }



        } catch (ParseException e) {
            this.errorOccured = true;
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callbackListener != null) {
            callbackListener.downloadCompleted(errorOccured);
        }
    }

    private void addSingleInstance(ArrayList<BaseModel> arrayList, BaseModel objectToAdd) {
        for (BaseModel arrObj : arrayList) {
            if (arrObj.getParseId().equals(objectToAdd.getParseId())) {
                return;
            }
        }
        arrayList.add(objectToAdd);
    }

    public interface DownloadTaskCompleted {
        void downloadedBuddy(Person buddy);
        void downloadedOuting(Outing outing);
        void downloadedExpense(Expense expense);
        void downloadCompleted(boolean error);
    }
}
