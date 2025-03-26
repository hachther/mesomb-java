package com.hachther.mesomb.operations;

import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.models.Transaction;
import com.hachther.mesomb.models.TransactionResponse;
import com.hachther.mesomb.util.RandomGenerator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Containing all operations provided by MeSomb Payment Service.
 */
public class PaymentOperation extends AOperation {
    public PaymentOperation(String applicationKey, String accessKey, String secretKey, String language) {
        super(applicationKey, accessKey, secretKey, language);
    }

    public PaymentOperation(String applicationKey, String accessKey, String secretKey) {
        super(applicationKey, accessKey, secretKey, "en");
    }

    /**
     * Collects a payment using the provided parameters.
     *
     * @param params a map containing the following keys:
     *               - amount: the amount to collect (required)
     *               - service: the payment service to use (required)
     *               - payer: the payer's identifier (required)
     *               - nonce: a unique string for each request (optional)
     *               - country: the country code (optional, defaults to "CM")
     *               - currency: the currency code (optional, defaults to "XAF")
     *               - fees: whether to include fees (optional, defaults to true)
     *               - conversion: whether to convert the amount (optional, defaults to false)
     *               - trxID: the transaction ID (optional)
     *               - location: Map (optional) containing the location of the customer with the following attributes: town, region and location all string.
     *               - customer: Map (optional) containing information about the customer: phone string, email: string, first_name string, last_name string, address string, town string, region string and country string
     *               - products: It is a List of products (optional). Each product are Map with the following attributes: name string, category string, quantity int and amount float
     *               - extra: a map of extra attributes (optional)
     *               - mode: the operation mode (optional, defaults to "synchronous")
     * @return a TransactionResponse object containing the response from the server
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public TransactionResponse makeCollect(Map<String, Object> params) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String endpoint = "payment/collect/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", params.get("amount"));
        body.put("service", params.get("service"));
        body.put("payer", params.get("payer"));
        body.put("country", params.getOrDefault("country", "CM"));
        body.put("currency", params.getOrDefault("currency", "XAF"));
        body.put("amount_currency", params.getOrDefault("currency", "XAF"));
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

        JSONParser parser = new JSONParser();
        return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), (String) params.getOrDefault("nonce", RandomGenerator.nonce()), body, (String) params.getOrDefault("mode", "synchronous"))));
    }

    /**
     * Make deposit in customer account
     *
     * @param params a map containing the following keys:
     *               - amount: the amount to collect (required)
     *               - service: the payment service to use (required)
     *               - receiver: the payer's identifier (required)
     *               - merchant: the merchant's identifier (required)
     *               - nonce: a unique string for each request (optional)
     *               - country: the country code (optional, defaults to "CM")
     *               - currency: the currency code (optional, defaults to "XAF")
     *               - trxID: the transaction ID (optional)
     *               - location: Map (optional) containing the location of the customer with the following attributes: town, region and location all string.
     *               - customer: Map (optional) containing information about the customer: phone string, email: string, first_name string, last_name string, address string, town string, region string and country string
     *               - products: It is a List of products (optional). Each product are Map with the following attributes: name string, category string, quantity int and amount float
     *               - extra: a map of extra attributes (optional)
     *               - mode: the operation mode (optional, defaults to "synchronous")
     * @return a TransactionResponse object containing the response from the server
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public TransactionResponse purchaseAirtime(Map<String, Object> params) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String endpoint = "payment/airtime/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", params.get("amount"));
        body.put("service", params.get("service"));
        body.put("receiver", params.get("receiver"));
        body.put("merchant", params.get("merchant"));
        body.put("country", params.getOrDefault("country", "CM"));
        body.put("currency", params.getOrDefault("currency", "XAF"));
        body.put("amount_currency", params.getOrDefault("currency", "XAF"));

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

        JSONParser parser = new JSONParser();
        return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), (String) params.getOrDefault("nonce", RandomGenerator.nonce()), body)));
    }

    /**
     * Make deposit in customer account
     *
     * @param params a map containing the following keys:
     *               - amount: the amount to collect (required)
     *               - service: the payment service to use (required)
     *               - receiver: the payer's identifier (required)
     *               - nonce: a unique string for each request (optional)
     *               - country: the country code (optional, defaults to "CM")
     *               - currency: the currency code (optional, defaults to "XAF")
     *               - conversion: whether to convert the amount (optional, defaults to false)
     *               - trxID: the transaction ID (optional)
     *               - location: Map (optional) containing the location of the customer with the following attributes: town, region and location all string.
     *               - customer: Map (optional) containing information about the customer: phone string, email: string, first_name string, last_name string, address string, town string, region string and country string
     *               - products: It is a List of products (optional). Each product are Map with the following attributes: name string, category string, quantity int and amount float
     *               - extra: a map of extra attributes (optional)
     *               - mode: the operation mode (optional, defaults to "synchronous")
     * @return a TransactionResponse object containing the response from the server
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public TransactionResponse makeDeposit(Map<String, Object> params) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String endpoint = "payment/deposit/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", params.get("amount"));
        body.put("service", params.get("service"));
        body.put("receiver", params.get("receiver"));
        body.put("country", params.getOrDefault("country", "CM"));
        body.put("currency", params.getOrDefault("currency", "XAF"));
        body.put("amount_currency", params.getOrDefault("currency", "XAF"));
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

        JSONParser parser = new JSONParser();
        return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), (String) params.getOrDefault("nonce", RandomGenerator.nonce()), body)));
    }

    /**
     * Get the current status of your service on MeSomb
     *
     * @return Application object containing the response from the server
     *
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     */
    public Application getStatus() throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException {
        String endpoint = "payment/status/";

        JSONParser parser = new JSONParser();
        return new Application((JSONObject) parser.parse(this.executeRequest("GET", endpoint, new Date())));
    }

    /**
     * Get transactions stored in MeSomb based on the list
     *
     * @param ids Ids of transactions to fetch
     * @param source Source of the transaction with possible values MESOMB, EXTERNAL
     *
     * @return Transaction[] of the transactions fetched
     *
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public Transaction[] getTransactions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String[] query = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            query[i] = "ids=" + ids[i];
        }
        String endpoint = "payment/transactions/check/?" + String.join("&", query) + "&source=" + source;

        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        Transaction[] transactions = new Transaction[response.size()];
        for (int i = 0; i < response.size(); i++) {
            transactions[i] = new Transaction((JSONObject) response.get(i));
        }
        return transactions;
    }

    /**
     * Get transactions stored in MeSomb based on the list
     *
     * @param ids Ids of transactions to fetch
     *
     * @return Transaction[] of the transactions fetched
     *
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public Transaction[] getTransactions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return this.getTransactions(ids, "MESOMB");
    }

    /**
     * Check transactions stored in MeSomb based on the list
     *
     * @param ids Ids of transactions to fetch
     *
     * @return Transaction[] of the transactions fetched
     *
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public Transaction[] checkTransactions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String[] query = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            query[i] = "ids=" + ids[i];
        }
        String endpoint = "payment/transactions/check/?" + String.join("&", query) + "&source=" + source;

        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        Transaction[] transactions = new Transaction[response.size()];
        for (int i = 0; i < response.size(); i++) {
            transactions[i] = new Transaction((JSONObject) response.get(i));
        }
        return transactions;
    }

    /**
     * Check transactions stored in MeSomb based on the list
     *
     * @param ids Ids of transactions to fetch
     *
     * @return Transaction[] of the transactions fetched
     *
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public Transaction[] checkTransactions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return this.checkTransactions(ids, "MESOMB");
    }

    /**
     * Refund a transaction
     * @param id the id of the transaction to refund
     * @param amount the amount to refund (optional)
     * @param currency the currency of the amount to refund (optional)
     * @param conversion whether to convert the amount (optional)
     *
     * @return a TransactionResponse object containing the response from the server
     *
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public TransactionResponse refundTransaction(String id, Double amount, String currency, Boolean conversion) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        String endpoint = "payment/refund/";

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        if (amount != null) {
            body.put("amount", amount);
        }
        if (currency != null) {
            body.put("currency", currency);
            body.put("amount_currency", currency);
        }
        if (conversion != null) {
            body.put("conversion", conversion);
        }

        JSONParser parser = new JSONParser();
        return new TransactionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), RandomGenerator.nonce(), body)));
    }

    public TransactionResponse refundTransaction(String id) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException, java.text.ParseException {
        return this.refundTransaction(id, null, null, null);
    }

    @Override
    public String getService() {
        return "payment";
    }
}
