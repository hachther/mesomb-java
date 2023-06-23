package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class Product {
    public final String name;
    public final String category;
    public final Integer quantity;
    public final Float amount;

    public Product(JSONObject obj) {
        this.name = (String) obj.get("name");
        this.category = (String) obj.getOrDefault("category", null);
        this.quantity = (Integer) obj.getOrDefault("quantity", null);
        this.amount = (Float) obj.getOrDefault("amount", null);
    }
}
