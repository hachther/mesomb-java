package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

public class Product {
    public final String id;
    public final String name;
    public final String category;
    public final Long quantity;
    public final Double amount;

    public Product(JSONObject obj) {
        System.out.println(obj);
        this.id = (String) obj.get("id");
        this.name = (String) obj.get("name");
        this.category = (String) obj.getOrDefault("category", null);
        this.quantity = obj.containsKey ("quantity") ? (Long) obj.get("quantity") : null;
        this.amount = obj.containsKey ("amount") ? (Double) obj.get("amount") : null;
    }
}
