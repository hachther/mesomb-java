package com.hachther.mesomb.operations;

import com.hachther.mesomb.MeSomb;
import com.hachther.mesomb.Signature;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.models.TransactionResponse;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Containing all operations provided by MeSomb Payment Service.
 * [Check the documentation here](https://mesomb.hachther.com/en/api/v1.1/schema/)
 */
public class PaymentOperation {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private final String applicationKey;
    private final String accessKey;
    private final String secretKey;

    public PaymentOperation(String applicationKey, String accessKey, String secretKey) {
        this.applicationKey = applicationKey;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private String buildUrl(String endpoint) {
        return MeSomb.apiBase + "/en/api/" + MeSomb.apiVersion + "/" + endpoint;
    }

    private String getAuthorization(String method, String endpoint, Date date, String nonce, TreeMap<String, String> headers, Map<String, Object> body) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String url = this.buildUrl(endpoint);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("accessKey", this.accessKey);
        credentials.put("secretKey", this.secretKey);

        return Signature.signRequest("payment", method, url, date, nonce, credentials, headers, body);
    }

    private String getAuthorization(String method, String endpoint, Date date, String nonce) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return this.getAuthorization(method, endpoint, date, nonce, null, null);
    }


    private void processClientException(int statusCode, String response) throws IOException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ServerException {
        String code = null;
        String message = response;
        if (message.startsWith("{")) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject data = (JSONObject) parser.parse(message);
                message = (String) data.get("detail");
                code = (String) data.get("code");
            } catch (ParseException ignored) {
            }
        }
        switch (statusCode) {
            case 404:
                throw new ServiceNotFoundException(message);
            case 403:
            case 401:
                throw new PermissionDeniedException(message);
            case 400:
                throw new InvalidClientRequestException(message, code);
            default:
                throw new ServerException(message, code);
        }
    }

    private String executeRequest(String method, String endpoint, Date date) throws IOException, MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        return this.executeRequest(method, endpoint, date, "", null, null);
    }
    private String executeRequest(String method, String endpoint, Date date, String nonce, Map<String, Object> body) throws IOException, MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        return this.executeRequest(method, endpoint, date, nonce, body, null);
    }
    private String executeRequest(String method, String endpoint, Date date, String nonce, Map<String, Object> body, String mode) throws IOException, MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        String url = this.buildUrl(endpoint);
        String authorization;
        if (method.equals("POST")) {
            authorization = this.getAuthorization(method, endpoint, date, nonce, new TreeMap<>() {{
                put("content-type", JSON.toString());
            }}, body);
        } else {
            authorization = this.getAuthorization(method, endpoint, date, nonce);
        }

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, body != null ? RequestBody.create(JSONObject.toJSONString(body), JSON) : null)
                .addHeader("x-mesomb-date", String.valueOf(date.getTime() / 1000))
                .addHeader("x-mesomb-nonce", nonce)
                .addHeader("Authorization", authorization)
                .addHeader("X-MeSomb-Application", this.applicationKey);
        if (mode != null) {
            builder = builder.addHeader("X-MeSomb-OperationMode", mode);
        }

        try (Response response = client.newCall(builder.build()).execute()) {
            if (response.code() >= 400) {
                assert response.body() != null;
                this.processClientException(response.code(), response.body().string());
            }
            assert response.body() != null;
            return response.body().string();
        }
    }

    /**
     * Collect money a user account
     *
     * @param amount       amount to collect
     * @param service      MTN, ORANGE, AIRTEL
     * @param payer        account number to collect from
     * @param date         date of the request
     * @param nonce        unique string on each request
     * @param country      country CM, NE
     * @param currency     code of the currency of the amount
     * @param feesIncluded if your want MeSomb to include and compute fees in the amount to collect
     * @param mode         asynchronous or synchronous
     * @param conversion   In case of foreign currently defined if you want to rely on MeSomb to convert the amount in the local currency
     * @param location     Map containing the location of the customer check the documentation
     * @param customer     Map containing information of the customer check the documentation
     * @param product      Map containing information of the product check the documentation
     * @param extra        Extra parameter to send in the body check the API documentation
     * @return TransactionResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public TransactionResponse makeCollect(
            float amount,
            String service,
            String payer,
            Date date,
            String nonce,
            String country,
            String currency,
            boolean feesIncluded,
            String mode,
            boolean conversion,
            Map<String, String> location,
            Map<String, String> customer,
            Map<String, String> product,
            Map<String, Object> extra) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/collect/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("service", service);
        body.put("payer", payer);
        body.put("country", country);
        body.put("currency", currency);
        body.put("fees", feesIncluded);
        body.put("conversion", conversion);

        if (location != null) {
            body.put("location", location);
        }

        if (customer != null) {
            body.put("customer", customer);
        }

        if (product != null) {
            body.put("product", product);
        }

        if (extra != null) {
            for (String key : extra.keySet()) {
                body.put(key, extra.get(key));
            }
        }

        JSONParser parser = new JSONParser();
        try {
            return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, date, nonce, body, mode)));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public TransactionResponse makeCollect(float amount, String service, String payer, Date date, String nonce) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        return this.makeCollect(amount, service, payer, date, nonce, "CM", "XAF", true, "synchronous", false, null, null, null, null);
    }

    public TransactionResponse makeCollect(float amount, String service, String payer, Date date, String nonce, String country, String currency, boolean feesIncluded, String mode) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.makeCollect(amount, service, payer, date, nonce, country, currency, feesIncluded, mode, false, null, null, null, null);
    }

    /**
     * Method to make deposit in a receiver mobile account.
     *
     * @param amount the amount of the transaction
     * @param service service code (MTN, ORANGE, AIRTEL, ...)
     * @param receiver receiver account (in the local phone number)
     * @param date date of the request
     * @param nonce Unique key generated for each transaction
     * @param country country code, 'CM' by default
     * @param currency currency of the transaction (XAF, XOF, ...) XAF by default
     * @param extra extra parameters to send in the body check the API documentation
     * @return TransactionResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public TransactionResponse makeDeposit(float amount, String service, String receiver, Date date, String nonce, String country, String currency, Map<String, Object> extra) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/deposit/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("service", service);
        body.put("receiver", receiver);
        body.put("country", country);
        body.put("currency", currency);

        if (extra != null) {
            for (String key : extra.keySet()) {
                body.put(key, extra.get(key));
            }
        }

        JSONParser parser = new JSONParser();
        try {
            return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, date, nonce, body)));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public TransactionResponse makeDeposit(float amount, String service, String receiver, Date date, String nonce) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.makeDeposit(amount, service, receiver, date, nonce, "CM", "XAF", null);
    }


    /**
     * Update security parameters of your service on MeSomb
     *
     * @param field which security field you want to update (check API documentation)
     * @param action action SET or UNSET
     * @param value value of the field
     * @param date date of the request
     * @return Application
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public Application updateSecurity(String field, String action, Object value, Date date) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/security/";

        Map<String, Object> body = new HashMap<>();
        body.put("field", field);
        body.put("action", action);
        if (!Objects.equals(action, "UNSET")) {
            body.put("value", value);
        }

        if (date == null) {
            date = new Date();
        }

        JSONParser parser = new JSONParser();
        try {
            return new Application((JSONObject) parser.parse(this.executeRequest("POST", endpoint, date, "", body)));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public Application updateSecurity(String field, String action) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.updateSecurity(field, action, null, null);
    }

    public Application updateSecurity(String field, String action, Object value) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.updateSecurity(field, action, value, null);
    }

    /**
     * Get the current status of your service on MeSomb
     *
     * @param date date of the request
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public Application getStatus(Date date) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/status/";

        if (date == null) {
            date = new Date();
        }

        JSONParser parser = new JSONParser();
        try {
            return new Application((JSONObject) parser.parse(this.executeRequest("GET", endpoint, date)));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public Application getStatus() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.getStatus(null);
    }

    /**
     * Get transactions stored in MeSomb based on the list
     *
     * @param ids Ids of transactions to fetch
     * @param date date to consider in the request
     * @return List of the transactions fetched
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public JSONArray getTransactions(String[] ids, Date date) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/transactions/?ids=" + String.join(",", ids);

        if (date == null) {
            date = new Date();
        }

        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(this.executeRequest("GET", endpoint, date));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public JSONArray getTransactions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.getTransactions(ids, null);
    }

    /**
     * Reprocess transaction at the operators level to confirm the status of a transaction
     * @param ids list of transaction ids
     * @param date date to consider in the request
     * @return List of the transactions processed
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public JSONArray checkTransactions(String[] ids, Date date) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/transactions/check/?ids=" + String.join(",", ids);

        if (date == null) {
            date = new Date();
        }

        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(this.executeRequest("GET", endpoint, date));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public JSONArray checkTransactions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.checkTransactions(ids, null);
    }
}
