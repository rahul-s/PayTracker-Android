package com.creativecapsule.paytracker.Repository;

import android.content.Context;

import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.Repository.Common.BaseRepository;
import com.creativecapsule.paytracker.Repository.Common.SQLiteImplementation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 30/06/15.
 */
public class PersonRepository extends BaseRepository {

    private static final String TABLE_NAME = "Person";

    private static final String COLUMN_KEY_OUTING = "outing";
    private static final String COLUMN_KEY_EMAIL = "email";

    public static boolean save (Context context, Person person){

        boolean status = false;
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);

        List<String> clauses = new ArrayList<String>();
        String clause = KEY_OBJECT_ID + "=" + "'" + person.getIdentifier() + "'";
        clauses.add(clause);

        List<Object> persons = sqLiteImplementation.getObjectsFromClassWithClauses(Person.class,clauses);
        if (persons == null || persons.size() == 0) {
            status = sqLiteImplementation.saveObject(person);
        }
        else {
            Person savedPerson = (Person)persons.get(0);
            savedPerson.setName(person.getName());
            savedPerson.setNickName(person.getNickName());
            //TODO: update all the fields.
            status = sqLiteImplementation.updateObject(savedPerson);
        }
        return status;
    }

    public static ArrayList<Person> getPersons(Context context) {
        ArrayList<Person> persons = new ArrayList<>();
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        List<Object> personObjects = sqLiteImplementation.getObjectsFromClass(Person.class);
        for (int i = 0; i < personObjects.size(); i++) {
            persons.add((Person) personObjects.get(i));
        }
        return persons;
    }

    //TODO: change this to fetch from the Outings table
    public static Person getPerson(Context context, String emailId) {
        ArrayList<Person> persons = new ArrayList<>();
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        List<String> clauses = new ArrayList<String>();
        String clause = COLUMN_KEY_EMAIL + "=" + "'" + emailId + "'";
        clauses.add(clause);
        List<Object> personObjects = sqLiteImplementation.getObjectsFromClassWithClauses(Person.class, clauses);
        if (personObjects != null && personObjects.size() > 0) {
            return (Person) personObjects.get(0);
        }
        return null;
    }

    public static Person getPersonByParseId(Context context, String parseId) {
        ArrayList<Person> persons = new ArrayList<>();
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        List<String> clauses = new ArrayList<String>();
        String clause = KEY_PARSE_ID + "=" + "'" + parseId + "'";
        clauses.add(clause);
        List<Object> personObjects = sqLiteImplementation.getObjectsFromClassWithClauses(Person.class, clauses);
        if (personObjects != null && personObjects.size() > 0) {
            return (Person) personObjects.get(0);
        }
        return null;
    }

    public static void clearTable(Context context) {
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        sqLiteImplementation.deleteAllObjectsFromClass(TABLE_NAME);
    }
}
