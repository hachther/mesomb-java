package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class Application {
    private final String key;
    private final String logo;
    private final JSONArray balances;
    private final JSONArray countries;
    private final String description;
    private final boolean isLive;
    private final String name;
    private final JSONObject security;
    private final String status;
    private final String url;

    public Application(JSONObject data) {
        this.key = (String) data.get("key");
        this.logo = (String) data.get("logo");
        this.balances = (JSONArray) data.get("balances");
        this.countries = (JSONArray) data.get("countries");
        this.description = (String) data.get("description");
        this.isLive = (boolean) data.get("is_live");
        this.name = (String) data.get("name");
        this.security = (JSONObject) data.get("security");
        this.status = (String) data.get("status");
        this.url = (String) data.get("url");
    }

    public String getKey() {
        return key;
    }

    public String getLogo() {
        return logo;
    }

    public JSONArray getCountries() {
        return countries;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLive() {
        return isLive;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public float getBalance(String country, String service) {
        float balance = 0;
        for (Object o : this.balances) {
            JSONObject bal = (JSONObject) o;
            if (country != null && bal.get("country") != country) {
                continue;
            }
            if (service != null && bal.get("service") != service) {
                continue;
            }

            balance += (float) bal.get("value");
        }
        return balance;
    }

    public float getBalance() {
        return this.getBalance(null, null);
    }

    public float getBalance(String country) {
        return this.getBalance(country, null);
    }

    public Object getSecurityField(String field) {
        return this.security.get(field);
    }
}
