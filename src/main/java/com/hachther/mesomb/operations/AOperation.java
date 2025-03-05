package com.hachther.mesomb.operations;

import com.hachther.mesomb.MeSomb;
import com.hachther.mesomb.Signature;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class AOperation {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private final String target;
    private final String accessKey;
    private final String secretKey;
    private final String language;

    public AOperation(String target, String accessKey, String secretKey, String language) {
        this.target = target;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.language = language;
    }

    /**
     * Get the service name
     * @return the service name
     */
    public abstract String getService();

    /**
     * Build the URL for the request
     * @param endpoint the endpoint to call
     * @return the URL
     */
    private String buildUrl(String endpoint) {
        return MeSomb.apiBase + "/api/" + MeSomb.apiVersion + "/" + endpoint;
    }

    /**
     * Generate the authorization header
     * @param method HTTP method
     * @param endpoint the endpoint to call
     * @param date the date of the request
     * @param nonce the nonce of the request
     * @param headers the headers of the request
     * @param body the body of the request
     * @return the authorization header
     * @throws MalformedURLException if the URL is not valid
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is not valid
     */
    private String getAuthorization(String method, String endpoint, Date date, String nonce, TreeMap<String, String> headers, Map<String, Object> body) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String url = this.buildUrl(endpoint);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("accessKey", this.accessKey);
        credentials.put("secretKey", this.secretKey);

        return Signature.signRequest("payment", method, url, date, nonce, credentials, headers, body);
    }

    /**
     * Generate the authorization header
     * @param method HTTP method
     * @param endpoint the endpoint to call
     * @param date the date of the request
     * @param nonce the nonce of the request
     * @return the authorization header
     * @throws MalformedURLException if the URL is not valid
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is not valid
     */
    private String getAuthorization(String method, String endpoint, Date date, String nonce) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return this.getAuthorization(method, endpoint, date, nonce, null, null);
    }


    /**
     * Process the client exception
     *
     * @param statusCode the status code of the response
     * @param response the response
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ServerException if the server has an error
     */
    private void processClientException(int statusCode, String response) throws ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ServerException {
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

    /**
     * Execute the request
     * @param method HTTP method
     * @param endpoint the endpoint to call
     * @param date the date of the request
     *
     * @return the response
     *
     * @throws IOException if the request fails
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is not valid
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ServerException if the server has an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     */
    String executeRequest(String method, String endpoint, Date date) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        return this.executeRequest(method, endpoint, date, "", null, null);
    }

    /**
     * Execute the request
     * @param method HTTP method
     * @param endpoint the endpoint to call
     * @param date the date of the request
     * @param nonce the nonce of the request
     * @param body the body of the request
     *
     * @return the response
     *
     * @throws IOException if the request fails
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is not valid
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ServerException if the server has an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     */
    String executeRequest(String method, String endpoint, Date date, String nonce, Map<String, Object> body) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        return this.executeRequest(method, endpoint, date, nonce, body, null);
    }

    /**
     * Execute the request
     * @param method HTTP method
     * @param endpoint the endpoint to call
     * @param date the date of the request
     * @param nonce the nonce of the request
     * @param body the body of the request
     * @param mode the operation mode
     *
     * @return the response
     *
     * @throws IOException if the request fails
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeyException if the key is not valid
     * @throws InvalidClientRequestException if the request is invalid
     * @throws ServerException if the server has an error
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     */
    String executeRequest(String method, String endpoint, Date date, String nonce, Map<String, Object> body, String mode) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidClientRequestException, ServerException, ServiceNotFoundException, PermissionDeniedException {
        String url = this.buildUrl(endpoint);
        String authorization;
        String trxID = null;
        if (body != null && body.containsKey("trxID")) {
            trxID = (String) body.get("trxID");
            body.remove("trxID");
        }
        if (!method.equals("GET")) {
            assert body != null;
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
                .addHeader("X-MeSomb-Source", "MeSombJava/" + MeSomb.version)
                .addHeader("Accept-Language", language);
        if (getService().equals("payment")) {
            builder = builder.addHeader("X-MeSomb-Application", target);
        }
        if (getService().equals("wallet")) {
            builder = builder.addHeader("X-MeSomb-Provider", target);
        }
        if (getService().equals("fundraising")) {
            builder = builder.addHeader("X-MeSomb-Fund", target);
        }
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

}
