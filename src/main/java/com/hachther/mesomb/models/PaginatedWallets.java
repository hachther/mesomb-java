package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PaginatedWallets extends APaginated {
    public Wallet[] results = null;

    public PaginatedWallets(JSONObject data) {
        super(data);
        JSONArray results = (JSONArray) data.getOrDefault("results", new JSONArray());
        this.results = new Wallet[results.size()];
        for (int i = 0; i < results.size(); i++) {
            this.results[i] = new Wallet((JSONObject) results.get(i));
        }
    }
}
