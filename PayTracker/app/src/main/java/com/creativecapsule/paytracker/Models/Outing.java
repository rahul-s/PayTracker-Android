package com.creativecapsule.paytracker.Models;

import com.creativecapsule.paytracker.Models.Common.BaseModel;

import java.util.ArrayList;

/**
 * Created by rahul on 30/06/15.
 */
public class Outing extends BaseModel {
    private String title;
    private ArrayList<Person> persons;

    public Outing() {
        this.title = "";
        this.persons = new ArrayList<>();
    }

    public Outing(String title, ArrayList<Person> persons) {
        this.title = title;
        this.persons = persons;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public void addPersons(ArrayList<Person> persons) {
        if (this.persons == null) {
            this.persons = new ArrayList<>();
        }
        this.persons.addAll(persons);
    }

    public String getBuddiesText() {
        String buddiesString = "";

        for (Person buddy : this.getPersons()) {
            if (buddiesString.equals("")) {
                buddiesString = buddy.getName();
            }
            else {
                buddiesString += ", " + buddy.getName();
            }
        }

        return buddiesString;
    }

    public boolean isPersonIncluded(Person person) {
        if (this.persons == null || this.persons.size() == 0) {
            return false;
        }
        for (Person outingBuddy : this.persons) {
            if (outingBuddy.getIdentifier() == person.getIdentifier()) {
                return true;
            }
        }
        return false;
    }
}
