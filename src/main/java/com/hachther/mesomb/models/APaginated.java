package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class APaginated {
    public int count;
    public String next = null;
    public String previous = null;

    public APaginated(JSONObject data) {
        this.count = ((Long) data.get("count")).intValue();
        this.next = (String) data.getOrDefault("next", null);
        this.previous = (String) data.getOrDefault("previous", null);
    }
}
