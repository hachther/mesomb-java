package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class Location {
    public final String town;
    public final String region;
    public final String country;

    public Location(JSONObject obj) {
        this.town = (String) obj.get("town");
        this.region = (String) obj.getOrDefault("region", null);
        this.country = (String) obj.getOrDefault("country", null);
    }
}
