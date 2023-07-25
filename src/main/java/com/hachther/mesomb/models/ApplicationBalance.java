package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class ApplicationBalance {
    public final String country;
    public final String currency;
    public final String provider;
    public final Double value;
    public final String service_name;

    public ApplicationBalance(JSONObject data) {
        this.country = (String) data.get("country");
        this.currency = (String) data.get("currency");
        this.provider = (String) data.get("provider");
        this.value = (Double) data.get("value");
        this.service_name = (String) data.get("service_name");
    }
}
