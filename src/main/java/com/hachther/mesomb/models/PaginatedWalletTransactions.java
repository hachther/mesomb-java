package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;

public class PaginatedWalletTransactions extends APaginated {
    public WalletTransaction[] results;

    public PaginatedWalletTransactions(JSONObject data) throws ParseException {
        super(data);
        JSONArray results = (JSONArray) data.getOrDefault("results", new JSONArray());
        this.results = new WalletTransaction[results.size()];
        for (int i = 0; i < results.size(); i++) {
            this.results[i] = new WalletTransaction((JSONObject) results.get(i));
        }
    }
}
