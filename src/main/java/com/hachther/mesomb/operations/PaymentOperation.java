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

    private String executeRequest(String method, String endpoint, Date date) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        return this.executeRequest(method, endpoint, date, "", null, null);
    }

    private String executeRequest(String method, String endpoint, Date date, String nonce, Map<String, Object> body) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        return this.executeRequest(method, endpoint, date, nonce, body, null);
    }

    private String executeRequest(String method, String endpoint, Date date, String nonce, Map<String, Object> body, String mode) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        String url = this.buildUrl(endpoint);
        String authorization;
        String trxID = null;
        if (body != null && body.containsKey("trxID")) {
            trxID = (String) body.get("trxID");
            body.remove("trxID");
        }
        if (method.equals("POST")) {
            authorization = this.getAuthorization(method, endpoint, date, nonce, new TreeMap<String, String>() {{
                put("content-type", JSON.toString());
            }}, body);
        } else {
            authorization = this.getAuthorization(method, endpoint, date, nonce);
        }

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(MeSomb.requestTimeout, TimeUnit.SECONDS).build();

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
        if (trxID != null) {
            builder = builder.addHeader("X-MeSomb-TrxID", trxID);
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
     * @param params object with the below information
     *               - amount: amount to collect
     *               - service: payment service with the possible values MTN, ORANGE, AIRTEL
     *               - payer: account number to collect from
     *               - date: date of the request
     *               - nonce: unique string on each request
     *               - country: 2 letters country code of the service (configured during your service registration in MeSomb)
     *               - currency: currency of your service depending on your country
     *               - fees: false if your want MeSomb fees to be computed and included in the amount to collect
     *               - mode: asynchronous or synchronous
     *               - conversion: true in case of foreign currently defined if you want to rely on MeSomb to convert the amount in the local currency
     *               - location: Map containing the location of the customer with the following attributes: town, region and location all string.
     *               - products: It is array of products. Each product are Map with the following attributes: name string, category string, quantity int and amount float
     *               - customer: a Map containing information about the customer: phone string, email: string, first_name string, last_name string, address string, town string, region string and country string
     *               - trxID: if you want to include your transaction ID in the request
     *               - extra: Map to add some extra attribute depending on the API documentation
     * @return {@link TransactionResponse}
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public TransactionResponse makeCollect(Map<String, Object> params) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/collect/";

        Date date = (Date) params.getOrDefault("date", new Date());

        Map<String, Object> body = new HashMap<>();
        body.put("amount", params.get("amount"));
        body.put("service", params.get("service"));
        body.put("payer", params.get("payer"));
        body.put("country", params.getOrDefault("country", "CM"));
        body.put("currency", params.getOrDefault("currency", "XAF"));
        body.put("fees", params.getOrDefault("fees", true));
        body.put("conversion", params.getOrDefault("conversion", false));

        if (params.getOrDefault("trxID", null) != null) {
            body.put("trxID", params.get("trxID"));
        }

        if (params.getOrDefault("location", null) != null) {
            body.put("location", params.get("location"));
        }

        if (params.getOrDefault("customer", null) != null) {
            body.put("customer", params.get("customer"));
        }

        if (params.getOrDefault("products", null) != null) {
            body.put("products", params.get("products"));
        }

        if (params.getOrDefault("product", null) != null) {
            List<Map<String, Object>> products = new ArrayList<>();
            products.add((Map<String, Object>) params.get("product"));
            body.put("products", products);
        }

        Map<String, Object> extra = (Map<String, Object>) params.getOrDefault("extra", null);
        if (extra != null) {
            for (String key : extra.keySet()) {
                body.put(key, extra.get(key));
            }
        }

        JSONParser parser = new JSONParser();
        try {
            return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, date, (String) params.get("nonce"), body, (String) params.getOrDefault("mode", "synchronous"))));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    /**
     * Method to make deposit in a receiver mobile account.
     *
     * @param params object with the below information
     *               - amount: amount to collect
     *               - service: payment service with the possible values MTN, ORANGE, AIRTEL
     *               - receiver: account number to depose money
     *               - date: date of the request
     *               - nonce: unique string on each request
     *               - country: 2 letters country code of the service (configured during your service registration in MeSomb)
     *               - currency: currency of your service depending on your country
     *               - conversion: true in case of foreign currently defined if you want to rely on MeSomb to convert the amount in the local currency
     *               - location: Map containing the location of the customer with the following attributes: town, region and location all string.
     *               - products: It is array of products. Each product are Map with the following attributes: name string, category string, quantity int and amount float
     *               - customer: a Map containing information about the customer: phone string, email: string, first_name string, last_name string, address string, town string, region string and country string
     *               - trxID: if you want to include your transaction ID in the request
     *               - extra: Map to add some extra attribute depending on the API documentation
     * @return TransactionResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public TransactionResponse makeDeposit(Map<String, Object> params) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/deposit/";

        Date date = (Date) params.getOrDefault("date", new Date());

        Map<String, Object> body = new HashMap<>();
        body.put("amount", params.get("amount"));
        body.put("service", params.get("service"));
        body.put("receiver", params.get("receiver"));
        body.put("country", params.getOrDefault("country", "CM"));
        body.put("currency", params.getOrDefault("currency", "XAF"));
        body.put("conversion", params.getOrDefault("conversion", false));

        if (params.getOrDefault("trxID", null) != null) {
            body.put("trxID", params.get("trxID"));
        }

        if (params.getOrDefault("location", null) != null) {
            body.put("location", params.get("location"));
        }

        if (params.getOrDefault("customer", null) != null) {
            body.put("customer", params.get("customer"));
        }

        if (params.getOrDefault("products", null) != null) {
            body.put("products", params.get("products"));
        }

        Map<String, Object> extra = (Map<String, Object>) params.getOrDefault("extra", null);
        if (extra != null) {
            for (String key : extra.keySet()) {
                body.put(key, extra.get(key));
            }
        }

        JSONParser parser = new JSONParser();
        try {
            return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, date, (String) params.get("nonce"), body)));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }


    /**
     * Update security parameters of your service on MeSomb
     *
     * @param field  which security field you want to update (check API documentation)
     * @param action action SET or UNSET
     * @param value  value of the field
     * @param date   date of the request
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
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public Application getStatus() throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/status/";

        JSONParser parser = new JSONParser();
        try {
            return new Application((JSONObject) parser.parse(this.executeRequest("GET", endpoint, new Date())));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    /**
     * Get transactions stored in MeSomb based on the list
     *
     * @param ids Ids of transactions to fetch
     * @return List of the transactions fetched
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public JSONArray getTransactions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/transactions/?ids=" + String.join(",", ids) + "&source=" + source;

        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public JSONArray getTransactions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.getTransactions(ids, "MESOMB");
    }

    /**
     * Reprocess transaction at the operators level to confirm the status of a transaction
     *
     * @param ids list of transaction ids
     * @return List of the transactions processed
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ServerException
     * @throws ServiceNotFoundException
     * @throws PermissionDeniedException
     * @throws InvalidClientRequestException
     */
    public JSONArray checkTransactions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException {
        String endpoint = "payment/transactions/check/?ids=" + String.join(",", ids) + "&source=" + source;

        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        } catch (ParseException e) {
            throw new ServerException("Issue to parse transaction response", "parsing-issue");
        }
    }

    public JSONArray checkTransactions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException {
        return this.checkTransactions(ids, "MESOMB");
    }
}
