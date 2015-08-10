package com.creativecapsule.paytracker.Managers;

import android.content.Context;
import android.util.Log;

import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.Repository.ExpenseRepository;
import com.creativecapsule.paytracker.Repository.OutingRepository;
import com.creativecapsule.paytracker.Repository.PersonRepository;
import com.creativecapsule.paytracker.Utility.AsyncTasks.DownloadDataTask;
import com.creativecapsule.paytracker.Utility.AsyncTasks.SaveExpenseTask;
import com.creativecapsule.paytracker.Utility.AsyncTasks.SaveOutingTask;
import com.creativecapsule.paytracker.Utility.PayTrackerApplication;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 08/08/15.
 */
public class ParseDataManager {
    private static ParseDataManager sharedManager;
    private Context managerContext;

    private static final String DEBUG_TAG = "ParseDataManager";

    //region Parse table and columns
    public static final String PARSE_KEY_OBJECT_ID = "ObjectId";

    public static final String PARSE_TABLE_PERSON = "Person";
    public static final String PARSE_KEY_PERSON_NAME = "name";
    public static final String PARSE_KEY_PERSON_EMAIL = "email";
    public static final String PARSE_KEY_PERSON_PASSWORD = "password";
    public static final String PARSE_KEY_PERSON_SIGNED_UP = "signedUp";

    public static final String PARSE_TABLE_BUDDIES = "Buddies";
    public static final String PARSE_KEY_BUDDIES_BUDDY_ONE = "buddyOne";
    public static final String PARSE_KEY_BUDDIES_BUDDY_TWO = "buddyTwo";

    public static final String PARSE_TABLE_OUTING = "Outing";
    public static final String PARSE_KEY_OUTING_NAME = "name";
    public static final String PARSE_KEY_OUTING_CREATED_BY = "createdBy";

    public static final String PARSE_TABLE_OUTING_PERSON = "OutingPerson";
    public static final String PARSE_KEY_OUTING_PERSON_OUTING = "outing";
    public static final String PARSE_KEY_OUTING_PERSON_PERSON = "person";

    public static final String PARSE_TABLE_OUTING_OWNER = "OutingOwner";
    public static final String PARSE_KEY_OUTING_OWNER_OUTING = "outing";
    public static final String PARSE_KEY_OUTING_OWNER_OWNER = "owner";

    public static final String PARSE_TABLE_EXPENSE = "Expense";
    public static final String PARSE_KEY_EXPENSE_OUTING = "outing";
    public static final String PARSE_KEY_EXPENSE_EXPENSE_BY = "expenseBy";
    public static final String PARSE_KEY_EXPENSE_AMOUNT = "amount";
    public static final String PARSE_KEY_EXPENSE_DESCRIPTION = "description";
    public static final String PARSE_KEY_EXPENSE_NOTE = "note";
    public static final String PARSE_KEY_EXPENSE_UPDATED_BY = "updatedBy";

    public static final String PARSE_TABLE_EXPENSE_PERSON = "ExpensePerson";
    public static final String PARSE_KEY_EXPENSE_PERSON_EXPENSE = "expense";
    public static final String PARSE_KEY_EXPENSE_PERSON_PERSON = "person";
    //endregion


    public ParseDataManager() {
        this.managerContext = PayTrackerApplication.getAppContext();
    }

    public static ParseDataManager getSharedManager() {
        if (sharedManager == null) {
            sharedManager = new ParseDataManager();
        }
        return sharedManager;
    }

    //region Exposed methods

    /**
     * Checks if the email is registered with the app.
     * The callback will send status true if the email is already registered, otherwise false.
     * @param email
     * @param callbackListener
     */
    public void isEmailRegistered(String email, final ParseDataManagerListener callbackListener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_PERSON);
        query.whereEqualTo(PARSE_KEY_PERSON_EMAIL, email);
        query.whereEqualTo(PARSE_KEY_PERSON_SIGNED_UP, true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        callbackListener.completed(true, false);
                    } else {
                        callbackListener.completed(false, false);
                    }
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    /**
     * Registers a person with the given details.
     * The callback will send status true if successfully registered, otherwise false.
     * @param email
     * @param password
     * @param name
     * @param callbackListener
     */
    public void registerPerson(final String email, final String password, final String name, final ParseDataManagerListener callbackListener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_PERSON);
        query.whereEqualTo(PARSE_KEY_PERSON_EMAIL, email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        //Person found with matching email.
                        ParseObject person = list.get(0);
                        boolean signedUp = person.getBoolean(PARSE_KEY_PERSON_SIGNED_UP);
                        if (signedUp) {
                            //The person has already signed up.
                            callbackListener.completed(false, false);
                        } else {
                            //The existing person has not signed up.
                            signUpPerson(person, name, password, callbackListener);
                        }
                    } else {
                        //Email not matching any existing person.
                        confirmRegister(email, password, name, callbackListener);
                    }
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    // This method creates an entry in the parse with given details. Invoke this when user is not registered.
    private void confirmRegister(String email, String password, String name, final ParseDataManagerListener callbackListener) {
        final Person person = new Person(name, email, "");
        final ParseObject parseObject = getParseObject(person);
        parseObject.put(PARSE_KEY_PERSON_PASSWORD, password);
        parseObject.put(PARSE_KEY_PERSON_SIGNED_UP, true);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    person.setParseId(parseObject.getObjectId());
                    savePerson(person);
                    callbackListener.completed(true, false);
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    // This method signs up an existing user. Invoke this method when email exists but nut signed up.
    private void signUpPerson(final ParseObject parsePerson, String name, String password, final ParseDataManagerListener callbackListener) {
        parsePerson.put(PARSE_KEY_PERSON_PASSWORD, password);
        parsePerson.put(PARSE_KEY_PERSON_SIGNED_UP, true);
        parsePerson.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Person person = getPerson(parsePerson);
                    savePerson(person);
                    callbackListener.completed(true, false);
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    /**
     * Validates the email and password.
     * The callback will send status true if successfully validated, false otherwise.
     * @param email
     * @param password
     * @param callbackListener
     */
    public void loginPerson(String email, String password, final ParseDataManagerListener callbackListener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_PERSON);
        query.whereEqualTo(PARSE_KEY_PERSON_EMAIL, email);
        query.whereEqualTo(PARSE_KEY_PERSON_PASSWORD, password);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        //Person found with matching email.
                        ParseObject parsePerson = list.get(0);
                        Person person = getPerson(parsePerson);
                        savePerson(person);
                        callbackListener.completed(true, false);
                    } else {
                        callbackListener.completed(false, false);
                    }
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    public void addPerson(final Person person, final ParseDataManagerListener callbackListener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_PERSON);
        query.whereEqualTo(PARSE_KEY_PERSON_EMAIL, person.getEmail());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        //Person found with matching email.
                        ParseObject parsePerson = list.get(0);
                        person.setParseId(parsePerson.getObjectId());
                        savePerson(person);
                        addBuddyRecord(person, new ParseDataManagerListener() {
                            @Override
                            public void completed(boolean status, boolean error) {
                                callbackListener.completed(status, error);
                            }
                        });
                    } else {
                        //Email not matching any existing person.
                        confirmAddPerson(person.getEmail(), person.getName(), callbackListener);
                    }
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    private void confirmAddPerson(String email, String name, final ParseDataManagerListener callbackListener) {
        final Person person = new Person(name, email, "");
        final ParseObject parseObject = getParseObject(person);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    person.setParseId(parseObject.getObjectId());
                    savePerson(person);
                    addBuddyRecord(person, new ParseDataManagerListener() {
                        @Override
                        public void completed(boolean status, boolean error) {
                            callbackListener.completed(status, error);
                        }
                    });

                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }

    private void addBuddyRecord(final Person buddy, final ParseDataManagerListener callbackListener) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_BUDDIES);
            Person self = UserAccountManager.getSharedManager().getLoggedInPerson();
            ParseObject parseBuddyOne = getParseObject(self);
            parseBuddyOne.fetchIfNeeded();
            ParseObject parseBuddyTwo = getParseObject(buddy);
            parseBuddyTwo.fetchIfNeeded();
            query.whereEqualTo(PARSE_KEY_BUDDIES_BUDDY_ONE, parseBuddyOne);
            query.whereEqualTo(PARSE_KEY_BUDDIES_BUDDY_TWO, parseBuddyTwo);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list != null && list.size() > 0) {
                            callbackListener.completed(false, false);
                        } else {
                            //Email not matching any existing person.
                            confirmAddBuddy(buddy, new ParseDataManagerListener() {
                                @Override
                                public void completed(boolean status, boolean error) {
                                    callbackListener.completed(status, error);
                                }
                            });
                        }
                    } else {
                        callbackListener.completed(false, true);
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            callbackListener.completed(false, true);
        }
    }

    private void confirmAddBuddy(Person buddy, final ParseDataManagerListener callbackListener) {
        try {
            Person self = UserAccountManager.getSharedManager().getLoggedInPerson();
            ParseObject parseBuddies = new ParseObject(PARSE_TABLE_BUDDIES);
            ParseObject parseBuddyOne = getParseObject(self);
            parseBuddyOne.fetchIfNeeded();
            ParseObject parseBuddyTwo = getParseObject(buddy);
            parseBuddyTwo.fetchIfNeeded();
            parseBuddies.put(PARSE_KEY_BUDDIES_BUDDY_ONE, parseBuddyOne);
            parseBuddies.put(PARSE_KEY_BUDDIES_BUDDY_TWO, parseBuddyTwo);
            parseBuddies.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        callbackListener.completed(true, false);
                    } else {
                        callbackListener.completed(false, false);
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            callbackListener.completed(false, false);
        }
    }

    public void saveOuting(Outing outing, final ParseDataManagerListener callbackListener) {
        SaveOutingTask saveOutingTask = new SaveOutingTask(outing, new SaveOutingTask.TaskCompletedListener() {
            @Override
            public void completed(Outing outing, boolean status) {
                if (status) {
                    saveOutingToDB(outing);
                    callbackListener.completed(true, false);
                }
                else {
                    callbackListener.completed(false, false);
                }
            }
        });
        saveOutingTask.execute();
    }

    public void saveExpense(Expense expense, final ParseDataManagerListener callbackListener) {
        SaveExpenseTask saveExpenseTask = new SaveExpenseTask(expense, new SaveExpenseTask.TaskCompletedListener() {
            @Override
            public void completed(Expense expense, boolean status) {
                if (status) {
                    saveExpenseToDB(expense);
                    callbackListener.completed(true, false);
                }
                else {
                    callbackListener.completed(false, false);
                }
            }
        });
        saveExpenseTask.execute();
    }

    public void deleteExpense(Expense expense, final ParseDataManagerListener callbackListener) {
        ParseObject parseExpense = getParseObject(expense);
        parseExpense.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callbackListener.completed(true, false);
                }
                else {
                    callbackListener.completed(false,false);
                }
            }
        });
    }

    public void downloadUserData(Person self, final ParseDataManagerListener callbackListener) {
        DownloadDataTask downloadDataTask = new DownloadDataTask(self, new DownloadDataTask.DownloadTaskCompleted() {
            @Override
            public void downloadedBuddy(Person buddy) {
                Log.d(DEBUG_TAG, "Saving buddy:" + buddy.getName());
                savePerson(buddy);
            }

            @Override
            public void downloadedOuting(Outing outing) {
                saveOutingToDB(outing);
            }

            @Override
            public void downloadedExpense(Expense expense) {
                saveExpenseToDB(expense);
            }

            @Override
            public void downloadCompleted(boolean error) {
                callbackListener.completed(true, error);
            }
        });
        downloadDataTask.execute();
    }
    //endregion

    //region Parse conversion methods

    /**
     * creates a Parse objects to be saved to parse.
     * The list will contain one object for the person
     * @param person
     * @return
     */
    public ParseObject getParseObject(Person person) {
        ParseObject parseObject = new ParseObject(ParseDataManager.PARSE_TABLE_PERSON);
        parseObject.put(ParseDataManager.PARSE_KEY_PERSON_EMAIL, person.getEmail());
        parseObject.put(ParseDataManager.PARSE_KEY_PERSON_NAME, person.getName());
        try {
            if (person.getParseId() != null && !person.getParseId().equals("")) {
                parseObject.setObjectId(person.getParseId());
                parseObject.fetchIfNeeded();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parseObject;
    }

    /**
     * creates a Parse object to be saved to parse.
     * @param outing
     * @return
     */
    public ParseObject getParseObject(Outing outing) {
        ParseObject parseOuting = new ParseObject(PARSE_TABLE_OUTING);
        parseOuting.put(ParseDataManager.PARSE_KEY_OUTING_NAME, outing.getTitle());
        try {
            if (outing.getParseId()!=null && !outing.getParseId().equals("")) {
                parseOuting.setObjectId(outing.getParseId());
                parseOuting.fetchIfNeeded();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parseOuting;
    }

    /**
     * creates a list of Parse objects to be saved to parse.
     * The list will contain one object for the Expense and one object each for the expense persons.
     * @param expense
     * @return
     */
    public ParseObject getParseObject(Expense expense) {
        ParseObject parseExpense = new ParseObject(PARSE_TABLE_EXPENSE);
        parseExpense.put(PARSE_KEY_EXPENSE_NOTE, expense.getNote());
        parseExpense.put(PARSE_KEY_EXPENSE_AMOUNT, expense.getAmount());
        parseExpense.put(PARSE_KEY_EXPENSE_DESCRIPTION, expense.getDescription());
        try {
            if (expense.getParseId() != null && !expense.getParseId().equals("")) {
                parseExpense.setObjectId(expense.getParseId());
                parseExpense.fetchIfNeeded();
            }

            ParseObject parseOuting = getParseObject(expense.getOuting());
            parseOuting.fetchIfNeeded();
            parseExpense.put(PARSE_KEY_EXPENSE_OUTING, parseOuting);

            ParseObject parsePerson = getParseObject(expense.getExpenseBy());
            parsePerson.fetchIfNeeded();
            parseExpense.put(PARSE_KEY_EXPENSE_EXPENSE_BY, parsePerson);

            Person updatedBy = UserAccountManager.getSharedManager().getLoggedInPerson();
            ParseObject parseUpdatedBy = getParseObject(updatedBy);
            parseUpdatedBy.fetchIfNeeded();
            parseExpense.put(PARSE_KEY_EXPENSE_UPDATED_BY, parseUpdatedBy);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseExpense;
    }

    public Person getPerson(ParseObject parseObject) {
        if (parseObject.getClassName().equals(PARSE_TABLE_PERSON)) {
            Person person = PersonRepository.getPersonByParseId(managerContext, parseObject.getObjectId());
            if (person == null) {
                person = new Person();
            }
            String name = parseObject.getString(PARSE_KEY_PERSON_NAME);
            String email = parseObject.getString(PARSE_KEY_PERSON_EMAIL);
            person.setName(name);
            person.setEmail(email);
            person.setParseId(parseObject.getObjectId());
            return person;
        }
        else {
            return null;
        }
    }

    public Outing getOuting(ParseObject parseOuting, ArrayList<ParseObject> parseOutingBuddies) {
        if (parseOuting.getClassName().equals(PARSE_TABLE_OUTING)) {
            Outing outing = OutingRepository.getOutingByParseId(managerContext, parseOuting.getObjectId());
            if (outing == null) {
                outing = new Outing();
            }
            String title = parseOuting.getString(PARSE_KEY_OUTING_NAME);
            ArrayList<Person> outingBuddies = new ArrayList<>();
            for (ParseObject parseOutingBuddy : parseOutingBuddies) {
                Person outingBuddy = getPerson(parseOutingBuddy);
                outingBuddies.add(outingBuddy);
            }
            outing.setTitle(title);
            outing.setPersons(outingBuddies);
            outing.setParseId(parseOuting.getObjectId());
            return outing;
        }
        else {
            return null;
        }
    }

    public Expense getExpense(ParseObject parseExpense, ArrayList<ParseObject> parseExpensePersons, Outing outing) {
        if (parseExpense.getClassName().equals(PARSE_TABLE_EXPENSE)) {
            Expense expense = ExpenseRepository.getExpenseByParseId(managerContext, parseExpense.getObjectId());
            if (expense == null) {
                expense = new Expense();
            }
            ParseObject parseExpenseBy = parseExpense.getParseObject(PARSE_KEY_EXPENSE_EXPENSE_BY);
            Person expenseBy = getPerson(parseExpenseBy);
            expense.setExpenseBy(expenseBy);
            ArrayList<Person> expenseFor = new ArrayList<>();
            for (ParseObject parseExpenseFor : parseExpensePersons) {
                Person expensePerson = getPerson(parseExpenseFor);
                expenseFor.add(expensePerson);
            }
            expense.setExpenseFor(expenseFor);
            int amount = parseExpense.getInt(PARSE_KEY_EXPENSE_AMOUNT);
            expense.setAmount(amount);
            expense.setOuting(outing);
            String description = parseExpense.getString(PARSE_KEY_EXPENSE_DESCRIPTION);
            expense.setDescription(description);
            String note = parseExpense.getString(PARSE_KEY_EXPENSE_NOTE);
            expense.setNote(note);
            expense.setParseId(parseExpense.getObjectId());
            return expense;
        }
        else {
            return null;
        }
    }
    //endregion



    //region Database methods
    private void saveOutingToDB(Outing outing) {
        OutingRepository.save(managerContext, outing);
    }

    private void saveExpenseToDB(Expense expense) {
        ExpenseRepository.save(managerContext, expense);
    }

    private void savePerson(Person person) {
        PersonRepository.save(managerContext, person);
    }
    //endregion

    public interface ParseDataManagerListener {
        void completed(boolean status, boolean error);
    }
}
