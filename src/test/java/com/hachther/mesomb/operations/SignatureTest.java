package com.hachther.mesomb.operations;

import com.hachther.mesomb.Signature;
import com.hachther.mesomb.util.RandomGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SignatureTest {
    private final Map<String, String> credentials = new HashMap<String, String>() {{
        put("accessKey", "c6c40b76-8119-4e93-81bf-bfb55417b392");
        put("secretKey", "fe8c2445-810f-4caa-95c9-778d51580163");
    }};
    @Test
    public void testSignatureWithGet() throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {
        String url = "http://127.0.0.1:8000/en/api/v1.1/payment/collect/";
        Assertions.assertEquals(Signature.signRequest("payment", "GET", url, new Date(1673827200000L), "fihser", credentials, null, null), "HMAC-SHA1 Credential=c6c40b76-8119-4e93-81bf-bfb55417b392/20230116/payment/mesomb_request, SignedHeaders=host;x-mesomb-date;x-mesomb-nonce, Signature=92866ff78427c739c1d48c9223a6133cde46ab5d");
    }
    @Test
    public void testSignatureWithPost() throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {
        String url = "http://127.0.0.1:8000/en/api/v1.1/payment/collect/";
        List<Map<String, Object>> products  = new ArrayList<>();
        products.add(new HashMap<String, Object>() {{
            put("id", "SKU001");
            put("name", "Sac a Main");
            put("category", "Sac");
        }});
        Map<String, Object> body = new HashMap<String, Object>() {{
            put("amount", 100f);
            put("service", "MTN");
            put("payer", "670000000");
            put("trxID", "1");
            put("products", products);
            put("customer", new HashMap<String, Object>(){{
                put("phone", "+237677550439");
                put("email", "fisher.bank@gmail.com");
                put("first_name", "Fisher");
                put("last_name", "BANK");
            }});
            put("location", new HashMap<String, Object>(){{
                put("town", "Douala");
                put("country", "Cameroun");
            }});
        }};
        Assertions.assertEquals(Signature.signRequest("payment", "POST", url, new Date(1673827200000L), "fihser", credentials, new TreeMap<String, String>() {{
            put("content-type", "application/json; charset=utf-8");
        }}, body), "HMAC-SHA1 Credential=c6c40b76-8119-4e93-81bf-bfb55417b392/20230116/payment/mesomb_request, SignedHeaders=content-type;host;x-mesomb-date;x-mesomb-nonce, Signature=06fa0b179edd88a40d3652f731f61defcf2e8d11");
    }
}
