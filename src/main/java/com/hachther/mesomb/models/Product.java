package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class Product {
    public final String name;
    public final String category;
    public final Long quantity;
    public final Float amount;

    public Product(JSONObject obj) {
        System.out.println(obj);
        this.name = (String) obj.get("name");
        this.category = (String) obj.getOrDefault("category", null);
        this.quantity = (Long) obj.getOrDefault("quantity", null);
        this.amount = (Float) obj.getOrDefault("amount", null);
    }
}
