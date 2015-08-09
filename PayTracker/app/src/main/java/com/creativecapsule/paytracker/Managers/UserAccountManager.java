package com.creativecapsule.paytracker.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.Repository.PersonRepository;
import com.creativecapsule.paytracker.Utility.Constants;
import com.creativecapsule.paytracker.Utility.PayTrackerApplication;

import java.util.ArrayList;

/**
 * Created by rahul on 08/08/15.
 */
public class UserAccountManager {
    private static UserAccountManager sharedManager = new UserAccountManager();
    private Context managerContext;

    private String email;
    private boolean emailRegistered;
    private boolean loggedIn;

    public static UserAccountManager getSharedManager() {
        return sharedManager;
    }

    private UserAccountManager() {
        this.managerContext = PayTrackerApplication.getAppContext();
        email = "";
        emailRegistered = false;
        validateLoggedInUser();
    }

    private void validateLoggedInUser() {
        SharedPreferences prefs = this.managerContext.getSharedPreferences(Constants.SHARED_PREFERENCE_USER_CREDENTIALS, this.managerContext.MODE_PRIVATE);
        email = prefs.getString(Constants.SHARED_PREFERENCE_KEY_EMAIL, "");
        if (email.equals("")) {
            loggedIn = false;
        }
        else {
            loggedIn = true;
        }
    }

    private void saveLoggedInUser() {
        SharedPreferences.Editor editor = this.managerContext.getSharedPreferences(Constants.SHARED_PREFERENCE_USER_CREDENTIALS, this.managerContext.MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFERENCE_KEY_EMAIL, email);
        editor.commit();
    }

    //region Exposed methods
    public void submitEmail(final String inputEmail, final UserAccountManagerListener callbackListener) {
        ParseDataManager.getSharedManager().isEmailRegistered(inputEmail, new ParseDataManager.ParseDataManagerListener() {
            @Override
            public void completed(boolean status, boolean error) {
                if (!error) {
                    email = inputEmail;
                    emailRegistered = status;
                    callbackListener.completed(true);
                } else {
                    callbackListener.completed(false);
                }
            }
        });
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isEmailRegistered() {
        return emailRegistered;
    }

    public void loginUser(String password, final UserAccountManagerListener callbackListener) {
        ParseDataManager.getSharedManager().loginPerson(email, password, new ParseDataManager.ParseDataManagerListener() {
            @Override
            public void completed(boolean status, boolean error) {
                if (status) {
                    loggedIn = true;
                    saveLoggedInUser();
                }
                callbackListener.completed(status);
            }
        });
    }

    public void registerUser(String name, String password, final UserAccountManagerListener callbackListener) {
        ParseDataManager.getSharedManager().registerPerson(email, password, name, new ParseDataManager.ParseDataManagerListener() {
            @Override
            public void completed(boolean status, boolean error) {
                if (status) {
                    loggedIn = true;
                    emailRegistered = true;
                    saveLoggedInUser();
                }
                callbackListener.completed(status);
            }
        });
    }

    public ArrayList<Person> getOutingBuddies(int outingId) {
        return PersonRepository.getOutingPersons(managerContext, outingId);
    }

    public ArrayList<Person> getPersons() {
        return PersonRepository.getPersons(managerContext);
    }

    public void savePerson(Person person) {
        PersonRepository.save(managerContext, person);
    }
    //endregion

    public interface UserAccountManagerListener {
        void completed(boolean status);
    }
}
