package com.creativecapsule.paytracker.Models.Misc;

import java.util.ArrayList;

/**
 * Created by rahul on 25/03/16.
 */
public class ContactItem {
    private String contactName;
    private ArrayList<String> contactNumbers;
    private String selectedContactNumber;

    public ContactItem() {
        this.contactName = "";
        this.contactNumbers = new ArrayList<>();
        this.selectedContactNumber = null;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public ArrayList<String> getContactNumbers() {
        return contactNumbers;
    }

    public void addContactNumber(String contactNumber) {
        this.contactNumbers.add(contactNumber);
    }

    public String getSelectedContactNumber() {
        return selectedContactNumber;
    }

    public void setSelectedContactNumber(String selectedContactNumber) {
        this.selectedContactNumber = selectedContactNumber;
    }
}
