package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.Objects;

public class ContributionResponse {
    public final boolean success;
    public final String message;
    public final Contribution contribution;
    public final String status;

    public ContributionResponse(JSONObject data) throws ParseException {
        this.success = (boolean) data.get("success");
        this.message = (String) data.get("message");
        this.contribution = new Contribution((JSONObject) data.get("contribution"));
        this.status = (String) data.get("status");
    }

    public boolean isOperationSuccess()
    {
        return this.success;
    }

    public boolean isContributionSuccess() {
        return this.success && Objects.equals(this.status, "SUCCESS");
    }
}
