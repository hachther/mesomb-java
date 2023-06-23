package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Transaction {
    public String pk;
    public String success;
    public String type;
    public Double amount;
    public Double fees;
    public String b_party;
    public String message;
    public String service;
    public String reference;
    public Date ts;
    public String country;
    public String currency;
    public String fin_trx_id;
    public Double trxamount;
    public Customer customer = null;
    public Location location = null;
    public Product[] products = null;

    public Transaction(JSONObject data) {
        this.pk = (String) data.get("pk");
        this.success = (String) data.get("success");
        this.type = (String) data.get("type");
        Object amount = data.get("amount");
        if (amount instanceof Double) {
            this.amount = (Double) amount;
        } else if (amount instanceof Long) {
            this.amount = Double.valueOf((Long) amount);
        }
        Object fees = data.get("fees");
        if (fees instanceof Double) {
            this.fees = (Double) fees;
        } else if (fees instanceof Long) {
            this.fees = Double.valueOf((Long) fees);
        }
        this.b_party = (String) data.get("b_party");
        this.message = (String) data.getOrDefault("message", null);
        this.service = (String) data.get("service");
        this.reference = (String) data.getOrDefault("reference", null);
        try {
            this.ts = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX").parse((String) data.get("ts"));
        } catch (Exception ignored) {

        }
        this.country = (String) data.get("country");
        this.currency = (String) data.get("currency");
        this.fin_trx_id = (String) data.getOrDefault("fin_trx_id", null);
        Object trxamount = data.getOrDefault("trxamount", null);
        if (trxamount != null) {
            if (trxamount instanceof Double) {
                this.trxamount = (Double) trxamount;
            } else if (trxamount instanceof Long) {
                this.trxamount = Double.valueOf((Long) trxamount);
            }
        }
        if (data.getOrDefault("customer", null) != null) {
            this.customer = new Customer((JSONObject) data.get("customer"));
        }
        if (data.getOrDefault("location", null) != null) {
            this.location = new Location((JSONObject) data.get("location"));
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