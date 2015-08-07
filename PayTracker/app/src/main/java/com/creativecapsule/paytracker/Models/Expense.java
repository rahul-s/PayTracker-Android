package com.creativecapsule.paytracker.Models;

import com.creativecapsule.paytracker.Models.Common.BaseModel;

import java.util.ArrayList;

/**
 * Created by rahul on 30/06/15.
 */
public class Expense extends BaseModel {
    private Person expenseBy;
    private ArrayList<Person> expenseFor;
    private int amount;
    private Outing outing;
    private String description;
    private String note;

    public Expense() {
        this.expenseBy = null;
        this.expenseFor = null;
        this.amount = 0;
        this.outing = null;
        this.description = "";
        this.note = "";
    }

    public Expense(Person expenseBy, ArrayList<Person> expenseFor, int amount, Outing outing, String description, String note) {
        this.expenseBy = expenseBy;
        this.expenseFor = expenseFor;
        this.amount = amount;
        this.outing = outing;
        this.description = description;
        this.note = note;
    }

    //region Setters

    public void setExpenseBy(Person expenseBy) {
        this.expenseBy = expenseBy;
    }

    public void setExpenseFor(ArrayList<Person> expenseFor) {
        this.expenseFor = expenseFor;
    }

    public void addExpenseFor(Person expenseFor) {
        if (this.expenseFor == null) {
            this.expenseFor = new ArrayList<>();
        }
        this.expenseFor.add(expenseFor);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setOuting(Outing outing) {
        this.outing = outing;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNote(String note) {
        this.note = note;
    }

    //endregion

    // region Getters
    public Person getExpenseBy() {
        return expenseBy;
    }

    public ArrayList<Person> getExpenseFor() {
        return expenseFor;
    }

    public int getAmount() {
        return amount;
    }

    public Outing getOuting() {
        return outing;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }

    public String getBuddiesText() {
        String buddiesString = "";
        for (Person buddy : this.getExpenseFor()) {
            if (buddiesString.equals("")) {
                buddiesString = buddy.getName();
            }
            else {
                buddiesString += ", " + buddy.getName();
            }
        }
        return buddiesString;
    }
    //endregion

    //region Helper methods

    public boolean isExpenseBy(Person person) {
        return (this.getExpenseBy().getIdentifier() == person.getIdentifier());
    }

    public boolean isExpenseFor(Person person) {
        for (Person personFor : getExpenseFor()) {
            if (personFor.getIdentifier() == person.getIdentifier()) {
                return true;
            }
        }
        return false;
    }

    public int getExpensePerPerson() {
        return amount/getExpenseFor().size();
    }
    //endregion
}
