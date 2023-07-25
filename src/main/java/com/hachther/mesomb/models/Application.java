package com.hachther.mesomb.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Objects;

public class Application {
    public final String key;
    public final String logo;
    public final ApplicationBalance[] balances;
    public final String[] countries;
    public final String description;
    public final boolean isLive;
    public final String name;
    public final Map<String, Object> security;
    public final String status;
    public final String url;

    public Application(JSONObject data) {
        this.key = (String) data.get("key");
        this.logo = (String) data.get("logo");
        JSONArray balances = (JSONArray) data.get("balances");
        this.balances = new ApplicationBalance[balances.size()];
        for (int i = 0; i < balances.size(); i++) {
            this.balances[i] = new ApplicationBalance((JSONObject) balances.get(i));
        }
        JSONArray countries = (JSONArray) data.get("countries");
        this.countries = new String[countries.size()];
        for (int i = 0; i < countries.size(); i++) {
            this.countries[i] = (String) countries.get(i);
        }
        this.description = (String) data.get("description");
        this.isLive = (boolean) data.get("is_live");
        this.name = (String) data.get("name");
        this.security = (JSONObject) data.get("security");
        this.status = (String) data.get("status");
        this.url = (String) data.get("url");
    }

    public float getBalance(String country, String service) {
        float balance = 0;
        for (ApplicationBalance bal : this.balances) {
            if (country != null && !Objects.equals(bal.country, country)) {
                continue;
            }
            if (service != null && !Objects.equals(bal.provider, service)) {
                continue;
            }

            balance += bal.value;
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
