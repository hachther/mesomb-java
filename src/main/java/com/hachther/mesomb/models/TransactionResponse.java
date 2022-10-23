package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Objects;

public class TransactionResponse {
    private final boolean success;
    private final String message;
    private final String redirect;
    private final Map<String, Object> data;
    private final String reference;
    private final String status;

    public TransactionResponse(JSONObject data) {
        this.success = (boolean) data.get("success");
        this.message = (String) data.get("message");
        this.redirect = (String) data.get("redirect");
        this.data = (Map<String, Object>) data.get("data");
        this.reference = (String) data.get("reference");
        this.status = (String) data.get("status");
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirect() {
        return redirect;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getReference() {
        return reference;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOperationSuccess()
    {
        return this.success;
    }

    public boolean isTransactionSuccess() {
        return this.success && Objects.equals(this.status, "SUCCESS");
    }
}
