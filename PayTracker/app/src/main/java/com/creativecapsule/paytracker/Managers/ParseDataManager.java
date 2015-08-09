package com.creativecapsule.paytracker.Managers;

import android.content.Context;

import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.Repository.ExpenseRepository;
import com.creativecapsule.paytracker.Repository.OutingRepository;
import com.creativecapsule.paytracker.Repository.PersonRepository;
import com.creativecapsule.paytracker.Utility.PayTrackerApplication;
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
    private static final String PARSE_KEY_OBJECT_ID = "ObjectId";

    private static final String PARSE_TABLE_PERSON = "Person";
    private static final String PARSE_KEY_PERSON_NAME = "name";
    private static final String PARSE_KEY_PERSON_EMAIL = "email";
    private static final String PARSE_KEY_PERSON_PASSWORD = "password";
    private static final String PARSE_KEY_PERSON_SIGNED_UP = "signedUp";

    private static final String PARSE_TABLE_OUTING = "Outing";
    private static final String PARSE_KEY_OUTING_NAME = "name";

    private static final String PARSE_TABLE_OUTING_PERSON = "OutingPerson";
    private static final String PARSE_KEY_OUTING_PERSON_OUTING = "outing";
    private static final String PARSE_KEY_OUTING_PERSON_PERSON = "person";

    private static final String PARSE_TABLE_OUTING_OWNER = "OutingOwner";
    private static final String PARSE_KEY_OUTING_OWNER_OUTING = "outing";
    private static final String PARSE_KEY_OUTING_OWNER_OWNER = "owner";

    private static final String PARSE_TABLE_EXPENSE = "Expense";
    private static final String PARSE_KEY_EXPENSE_OUTING = "outing";
    private static final String PARSE_KEY_EXPENSE_EXPENSE_BY = "expenseBy";
    private static final String PARSE_KEY_EXPENSE_AMOUNT = "amount";
    private static final String PARSE_KEY_EXPENSE_DESCRIPTION = "description";
    private static final String PARSE_KEY_EXPENSE_NOTE = "note";
    private static final String PARSE_KEY_EXPENSE_UPDATED_BY = "updatedBy";

    private static final String PARSE_TABLE_EXPENSE_PERSON = "ExpensePerson";
    private static final String PARSE_KEY_EXPENSE_PERSON_EXPENSE = "expense";
    private static final String PARSE_KEY_EXPENSE_PERSON_PERSON = "person";
    //endregion


    private ParseDataManager() {
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
                }
                else {
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
                        callbackListener.completed(true,false);
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
                    callbackListener.completed(true, false);
                } else {
                    callbackListener.completed(false, true);
                }
            }
        });
    }
    //endregion

    //region Parse conversion methods

    /**
     * creates a Parse objects to be saved to parse.
     * The list will contain one object for the person
     * @param person
     * @return
     */
    private ParseObject getParseObject(Person person) {
        ParseObject parseObject = new ParseObject(PARSE_TABLE_PERSON);
        parseObject.put(PARSE_KEY_PERSON_EMAIL, person.getEmail());
        parseObject.put(PARSE_KEY_PERSON_NAME, person.getName());

        return parseObject;
    }

    /**
     * creates a list of Parse objects to be saved to parse.
     * The list will contain one object for the outing, one Parse object for each person in outing buddies,
     * and a Parse object for the Owner.
     * @param outing
     * @return
     */
    private List<ParseObject> getParseObjects(Outing outing) {
        ArrayList<ParseObject> parseObjects = new ArrayList<>();
        //  TODO: populate the list.
        return parseObjects;
    }

    /**
     * creates a list of Parse objects to be saved to parse.
     * The list will contain one object for the Expense and one object each for the expense persons.
     * @param expense
     * @return
     */
    private List<ParseObject> getParseObjects(Expense expense) {
        ArrayList<ParseObject> parseObjects = new ArrayList<>();
        // TODO: populate the list
        return parseObjects;
    }

    private Person getPerson(ParseObject parseObject) {
        if (parseObject.getClassName().equals(PARSE_TABLE_PERSON)) {
            String name = parseObject.getString(PARSE_KEY_PERSON_NAME);
            String email = parseObject.getString(PARSE_KEY_PERSON_EMAIL);
            Person person = new Person(name, email, "");
            person.setParseId(parseObject.getObjectId());
            return person;
        }
        else {
            return null;
        }
    }
    //endregion



    //region Database methods
    private void saveOuting(Outing outing) {
        OutingRepository.save(managerContext, outing);
    }

    private void saveExpense(Expense expense) {
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
