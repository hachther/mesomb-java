package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class Customer {
    /// The email of the customer
    public final String email;

    /// The phone number of the customer
    public final String phone;

    /// The town of the customer
    public final String town;

    /// The region of the customer
    public final String region;

    /// The country of the customer
    public final String country;

    /// The first name of the customer
    public final String firstName;

    /// The last name of the customer
    public final String lastName;

    /// The address of the customer
    public final String address;

    public Customer(JSONObject obj) {
        this.email = (String) obj.getOrDefault("email", null);
        this.phone = (String) obj.getOrDefault("phone", null);
        this.town = (String) obj.getOrDefault("town", null);
        this.region = (String) obj.getOrDefault("region", null);
        this.country = (String) obj.getOrDefault("country", null);
        this.firstName = (String) obj.getOrDefault("first_name", null);
        this.lastName = (String) obj.getOrDefault("last_name", null);
        this.address = (String) obj.getOrDefault("address", null);
    }
}
