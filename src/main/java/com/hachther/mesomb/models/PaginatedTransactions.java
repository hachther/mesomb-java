package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;

public class PaginatedTransactions extends APaginated {
    public Transaction[] results = null;

    public PaginatedTransactions(JSONObject data) throws ParseException {
        super(data);
        JSONArray results = (JSONArray) data.getOrDefault("results", new JSONArray());
        this.results = new Transaction[results.size()];
        for (int i = 0; i < results.size(); i++) {
            this.results[i] = new Transaction((JSONObject) results.get(i));
        }
    }
}
