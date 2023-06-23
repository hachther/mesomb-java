package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

import java.util.Objects;

public class TransactionResponse {
    public final boolean success;
    public final String message;
    public final String redirect;
    public final Transaction transaction;
    public final String reference;
    public final String status;

    public TransactionResponse(JSONObject data) {
        this.success = (boolean) data.get("success");
        this.message = (String) data.get("message");
        this.redirect = (String) data.get("redirect");
        this.transaction = new Transaction((JSONObject) data.get("transaction"));
        this.reference = (String) data.get("reference");
        this.status = (String) data.get("status");
    }

    public boolean isOperationSuccess()
    {
        return this.success;
    }

    public boolean isTransactionSuccess() {
        return this.success && Objects.equals(this.status, "SUCCESS");
    }
}
