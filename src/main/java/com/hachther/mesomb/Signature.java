package com.hachther.mesomb;

import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used to generate the signature for each request.
 *
 * This signature generated will be added at the "Authorisation" header for each request
 */
public class Signature {

    /**
     * Convert array of by to hexadecimal varchar
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Hash a string with SHA1 algorithm
     * @param input string to hash
     * @return hashed string
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        return bytesToHex(md.digest(input.getBytes()));
    }

    public static String hmacSha1(String key, String input) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
        return bytesToHex(mac.doFinal(input.getBytes()));
    }

    /**
     *
     * @param service name of the mesomb service (Expl: payment)
     * @param method HTTP method
     * @param url the url of the HTTP Request
     * @param date the datetime of the transaction
     * @param nonce random generated string that should be unique on each POST request
     * @param credentials MeSomb credential (accessKey and the secretKey)
     * @param headers HTTP headers of the request
     * @param body body of the request in case of POST request
     * @return
     * @throws MalformedURLException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String signRequest(String service, String method, String url, Date date, String nonce, Map<String, String> credentials, TreeMap<String, String> headers, Map<String, Object> body) throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = MeSomb.algorithm;
        URL parse = new URL(url);
        String canonicalQuery = parse.getQuery() != null ? parse.getQuery() : "";

        long timestamp = date.getTime() / 1000;

        if (headers == null) {
            headers = new TreeMap<>();
        }
        headers.put("host", parse.getProtocol() + "://" + parse.getHost() + (parse.getPort() > 0 ? ":" + parse.getPort() : ""));
        headers.put("x-mesomb-date", String.valueOf(timestamp));
        headers.put("x-mesomb-nonce", nonce);

        // String[] headersKeys = (String[]) headers.keySet().toArray();
        String[] headersTokens = new String[headers.size()];
        String[] headersKeys = new String[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersTokens[i] = key + ":" + headers.get(key);
            headersKeys[i] = key;
            i ++;
        }
        String canonicalHeaders = String.join("\n", headersTokens);

        String payloadHash = sha1(body != null ? JSONObject.toJSONString(body) .replace("\\/", "/"): "{}");

        String signedHeaders = String.join(";", headersKeys);

        String path;
        try {
            path = URLEncoder.encode(parse.getPath(), "UTF-8").replaceAll("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String canonicalRequest = method + "\n" + path + "\n" + canonicalQuery + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + payloadHash;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");


        String scope = dateFormat.format(date) + "/" + service + "/mesomb_request";

        String stringToSign = algorithm + "\n" + timestamp + "\n" + scope + "\n" + sha1(canonicalRequest);

        String signature = hmacSha1(credentials.get("secretKey"), stringToSign);

        return algorithm + " Credential=" + credentials.get("accessKey") + "/" + scope + ", SignedHeaders=" + signedHeaders + ", Signature=" + signature;
    }
}
