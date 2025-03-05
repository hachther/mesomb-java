package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Wallet {
    private final JSONObject _data;

    public final Long id;
    public final String number;
    public final String country;
    public final String status;
    public Date lastActivity = null;
    public Double balance = null;
    public String firstName = null;
    public final String lastName;
    public String email = null;
    public final String phoneNumber;
    public final String gender;

    public Wallet(JSONObject data) {
        _data = data;

        this.id = (Long) data.get("identifier");
        this.number = (String) data.get("number");
        this.country = (String) data.get("country");
        this.status = (String) data.get("status");
        if (data.getOrDefault("last_activity", null) != null) {
            try {
                this.lastActivity = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse((String) data.get("last_activity"));
            } catch (Exception ignored) {}
        }
        if (data.getOrDefault("balance", null) != null) {
            this.balance = (Double) data.get("balance");
        }
        this.firstName = (String) data.getOrDefault("first_name", null);
        this.lastName = (String) data.get("last_name");
        this.email = (String) data.getOrDefault("email", null);
        this.phoneNumber = (String) data.get("phone_number");
        this.gender = (String) data.get("gender");
    }

    public JSONObject getData() {
        return _data;
    }
}
