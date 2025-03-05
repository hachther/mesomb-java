package com.hachther.mesomb.models;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WalletTransaction {
    public JSONObject _data;

    public Long id;
    public String status;
    public String type;
    public Double amount;
    public int direction;
    public Long wallet;
    public Double balanceAfter;
    public Date date;
    public String country;
    public String finTrxId;
    public String message;

    public WalletTransaction(JSONObject data) throws ParseException {
        this._data = data;

        this.id = (Long) data.get("id");
        this.status = (String) data.get("status");
        this.type = (String) data.get("type");
        this.amount = (Double) data.get("amount");
        this.direction = Integer.parseInt(data.get("direction").toString());
        this.wallet = (Long) data.get("wallet");
        this.balanceAfter = (Double) data.get("balance_after");
        this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse((String) data.get("date"));
        this.country = (String) data.get("country");
        this.finTrxId = (String) data.get("fin_trx_id");
        this.message = (String) data.get("message");
    }

    public JSONObject getData() {
        return this._data;
    }
}
