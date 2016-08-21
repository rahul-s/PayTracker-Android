package com.creativecapsule.paytracker.Models;

import com.creativecapsule.paytracker.Models.Common.BaseModel;

/**
 * Created by rahul on 30/06/15.
 */
public class Person extends BaseModel {

    private String phoneNumber;
    private String email;
    private String name;
    private String nickName;

    public Person() {
        this.phoneNumber = "";
        this.name = "";
        this.email = "";
        this.nickName = "";
    }

    public Person(String name, String phone_number, String nickName) {
        this.name = name;
        this.phoneNumber = phone_number;
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
