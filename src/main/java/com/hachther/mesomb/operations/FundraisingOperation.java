package com.hachther.mesomb.operations;

import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Contribution;
import com.hachther.mesomb.models.ContributionResponse;
import com.hachther.mesomb.util.RandomGenerator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FundraisingOperation extends AOperation {
    public FundraisingOperation(String fundKey, String accessKey, String secretKey, String language) {
        super(fundKey, accessKey, secretKey, language);
    }

    public FundraisingOperation(String fundKey, String accessKey, String secretKey) {
        super(fundKey, accessKey, secretKey, "en");
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
     *               - anonymous: whether the contribution is anonymous (optional, defaults to false)
     *               - accept_terms: whether the terms are accepted (optional, defaults to true)
     *               - trxID: the contribution ID (optional)
     *               - location: Map (optional) containing the location of the customer with the following attributes: town, region and location all string.
     *               - contact: Map (optional) containing information about the customer: phone_number string
     *               - full_name: Map (optional) containing information about the customer: first_name string, last_name string
     *               - mode: the operation mode (optional, defaults to "synchronous")
     * @return a ContributionResponse object containing the response from the server
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws ServerException if the server encounters an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ParseException if the response cannot be parsed
     */
    public ContributionResponse makeContribution(Map<String, Object> params) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String endpoint = "fundraising/contribute/";

        Map<String, Object> body = new HashMap<>();
        body.put("service", params.get("service"));
        body.put("amount", params.get("amount"));
        body.put("payer", params.get("payer"));
        body.put("country", params.getOrDefault("country", "CM"));
        body.put("amount_currency", params.getOrDefault("currency", "XAF"));
        body.put("conversion", params.getOrDefault("conversion", false));
        body.put("anonymous", params.getOrDefault("anonymous", false));
        body.put("accept_terms", params.getOrDefault("accept_terms", true));

        if (params.getOrDefault("trxID", null) != null) {
            body.put("trxID", params.get("trxID"));
        }

        if (params.getOrDefault("full_name", null) != null) {
            body.put("full_name", params.get("full_name"));
        }

        if (params.getOrDefault("contact", null) != null) {
            body.put("contact", params.get("contact"));
        }

        JSONParser parser = new JSONParser();
        return new ContributionResponse((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), (String) params.getOrDefault("nonce", RandomGenerator.nonce()), body, (String) params.getOrDefault("mode", "synchronous"))));
    }


    /**
     * Get contributions stored in MeSomb based on the list
     *
     * @param ids Ids of contributions to fetch
     * @param source Source of the contribution with possible values MESOMB, EXTERNAL
     *
     * @return Contribution[] of the contributions fetched
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
    public Contribution[] getContributions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String endpoint = "fundraising/contributions/?ids=" + String.join(",", ids) + "&source=" + source;

        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        Contribution[] contributions = new Contribution[response.size()];
        for (int i = 0; i < response.size(); i++) {
            contributions[i] = new Contribution((JSONObject) response.get(i));
        }
        return contributions;
    }

    /**
     * Get contributions stored in MeSomb based on the list
     *
     * @param ids Ids of contributions to fetch
     *
     * @return Contribution[] of the contributions fetched
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
    public Contribution[] getContributions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return this.getContributions(ids, "MESOMB");
    }

    /**
     * Check contributions stored in MeSomb based on the list
     *
     * @param ids Ids of contributions to fetch
     *
     * @return Contribution[] of the contributions fetched
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
    public Contribution[] checkContributions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String endpoint = "fundraising/contributions/check/?ids=" + String.join(",", ids) + "&source=" + source;

        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        Contribution[] contributions = new Contribution[response.size()];
        for (int i = 0; i < response.size(); i++) {
            contributions[i] = new Contribution((JSONObject) response.get(i));
        }
        return contributions;
    }

    /**
     * Check contributions stored in MeSomb based on the list
     *
     * @param ids Ids of contributions to fetch
     *
     * @return Contribution[] of the contributions fetched
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
    public Contribution[] checkContributions(String[] ids) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return this.checkContributions(ids, "MESOMB");
    }



    @Override
    public String getService() {
        return "fundraising";
    }
}
