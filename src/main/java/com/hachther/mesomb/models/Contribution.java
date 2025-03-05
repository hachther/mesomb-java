package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

import java.text.ParseException;

public class Contribution extends ATransaction {
    public Customer contributor = null;

    public Contribution(JSONObject data) throws ParseException {
        super(data);
        if (data.getOrDefault("contributor", null) != null) {
            this.contributor = new Customer((JSONObject) data.get("contributor"));
        }
    }
}