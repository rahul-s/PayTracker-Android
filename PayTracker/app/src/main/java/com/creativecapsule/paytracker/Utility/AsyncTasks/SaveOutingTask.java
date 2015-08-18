package com.creativecapsule.paytracker.Utility.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.creativecapsule.paytracker.Managers.ParseDataManager;
import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

/**
 * Created by rahul on 09/08/15.
 */
public class SaveOutingTask extends AsyncTask<Void, Integer, Boolean> {

    private static final String DEBUG_TAG = "SaveOutingTask";
    private Outing outing;
    private TaskCompletedListener callbackListener;

    public SaveOutingTask(Outing outing, TaskCompletedListener listener) {
        this.outing = outing;
        this.callbackListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            boolean isNewOuting = true;
            if (outing.getParseId()!=null && !outing.getParseId().equals("")){
                isNewOuting = false;
            }

            //save outing
            ParseObject parseOuting = ParseDataManager.getSharedManager().getParseObject(outing);
            parseOuting.save();
            outing.setParseId(parseOuting.getObjectId());

            //save outing person
            for (Person outingPerson : outing.getPersons()){
                saveOutingPerson(outing, outingPerson);
            }

            if (isNewOuting) {
                // Assign self as owner of this outing if newly created.
                Person outingOwner = UserAccountManager.getSharedManager().getLoggedInPerson();
                ParseObject outingOwnerObj = ParseDataManager.getSharedManager().getParseObject(outingOwner);
                parseOuting.put(ParseDataManager.PARSE_KEY_OUTING_CREATED_BY, outingOwnerObj);
                parseOuting.save();
                saveOutingOwner(outing, outingOwner);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (callbackListener != null) {
            callbackListener.completed(outing, aBoolean);
        }
    }




    private void saveOutingPerson(Outing outing, Person person) {
        ParseObject parseObject;
        ParseObject parseOuting = ParseDataManager.getSharedManager().getParseObject(outing);
        ParseObject parsePerson = ParseDataManager.getSharedManager().getParseObject(person);
        ParseQuery<ParseObject> outingPersonQuery = ParseQuery.getQuery(ParseDataManager.PARSE_TABLE_OUTING_PERSON);
        outingPersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_OUTING_PERSON_OUTING, parseOuting);
        outingPersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_OUTING_PERSON_PERSON, parsePerson);
        try {
            parseObject = outingPersonQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
            parseObject = new ParseObject(ParseDataManager.PARSE_TABLE_OUTING_PERSON);
            parseObject.put(ParseDataManager.PARSE_KEY_OUTING_PERSON_PERSON, parsePerson);
            parseObject.put(ParseDataManager.PARSE_KEY_OUTING_PERSON_OUTING, parseOuting);

        }
        try {
            parseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveOutingOwner(Outing outing, Person person) {
        ParseObject parseObject;
        ParseObject parseOuting = ParseDataManager.getSharedManager().getParseObject(outing);
        ParseObject parsePerson = ParseDataManager.getSharedManager().getParseObject(person);
        ParseQuery<ParseObject> outingPersonQuery = ParseQuery.getQuery(ParseDataManager.PARSE_TABLE_OUTING_OWNER);
        outingPersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_OUTING_OWNER_OUTING, parseOuting);
        outingPersonQuery.whereEqualTo(ParseDataManager.PARSE_KEY_OUTING_OWNER_OWNER, parsePerson);
        try {
            parseObject = outingPersonQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
            parseObject = new ParseObject(ParseDataManager.PARSE_TABLE_OUTING_OWNER);
            parseObject.put(ParseDataManager.PARSE_KEY_OUTING_OWNER_OWNER, parsePerson);
            parseObject.put(ParseDataManager.PARSE_KEY_OUTING_OWNER_OUTING, parseOuting);
        }
        try {
            parseObject.save();
            Log.d(DEBUG_TAG, "Outing owner saved.");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    public interface TaskCompletedListener {
        void completed(Outing outing, boolean status);
    }
}
