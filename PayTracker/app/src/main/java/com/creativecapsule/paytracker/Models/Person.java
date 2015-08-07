package com.creativecapsule.paytracker.Models;

import com.creativecapsule.paytracker.Models.Common.BaseModel;

/**
 * Created by rahul on 30/06/15.
 */
public class Person extends BaseModel {

    private String name;
    private String nickName;

    public Person() {
        this.name = "";
        this.nickName = "";
    }

    public Person(String name, String nickName) {
        this.name = name;
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
}
