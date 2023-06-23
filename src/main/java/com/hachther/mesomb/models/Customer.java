package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class Customer {
    public final String email;
    public final String phone;
    public final String town;
    public final String region;
    public final String country;
    public final String first_name;
    public final String last_name;
    public final String address;

    public Customer(JSONObject obj) {
        this.email = (String) obj.getOrDefault("email", null);
        this.phone = (String) obj.getOrDefault("phone", null);
        this.town = (String) obj.getOrDefault("town", null);
        this.region = (String) obj.getOrDefault("region", null);
        this.country = (String) obj.getOrDefault("country", null);
        this.first_name = (String) obj.getOrDefault("first_name", null);
        this.last_name = (String) obj.getOrDefault("last_name", null);
        this.address = (String) obj.getOrDefault("address", null);
    }
}
