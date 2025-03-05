package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;

public class Transaction extends ATransaction {
    public Customer customer = null;
    public Product[] products = null;

    public Transaction(JSONObject data) throws ParseException {
        super(data);
        if (data.getOrDefault("customer", null) != null) {
            this.customer = new Customer((JSONObject) data.get("customer"));
        }

        if (data.getOrDefault("products", null) != null) {
            JSONArray products = (JSONArray) data.get("products");
            this.products = new Product[products.size()];
            for (int i = 0; i < products.size(); i++) {
                this.products[i] = new Product((JSONObject) products.get(i));
            }
        }
    }
}