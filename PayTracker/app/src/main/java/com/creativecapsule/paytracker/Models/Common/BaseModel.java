package com.creativecapsule.paytracker.Models.Common;

/**
 * Created by soniya on 3/30/15.
 */
public class BaseModel {

    protected int identifier;

    protected String parseId;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }
}
