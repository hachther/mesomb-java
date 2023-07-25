package com.hachther.mesomb.operations;

import com.hachther.mesomb.MeSomb;
import com.hachther.mesomb.models.Transaction;
import com.hachther.mesomb.util.RandomGenerator;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Application;
import com.hachther.mesomb.models.TransactionResponse;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentOperationTest {
    private final String applicationKey = "2bb525516ff374bb52545bf22ae4da7d655ba9fd";
    private final String accessKey = "c6c40b76-8119-4e93-81bf-bfb55417b392";
    private final String secretKey = "fe8c2445-810f-4caa-95c9-778d51580163";

    @BeforeEach
    public void onSetup() {
        MeSomb.apiBase = "http://192.168.8.107:8000";
        MeSomb.requestTimeout = 60;
    }

    @Test
    public void testMakeCollectWithServiceNotFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.makeCollect(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testMakeCollectWithPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.makeCollect(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testMakeCollectWithInvalidAmount() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        Exception exception = assertThrows(InvalidClientRequestException.class, () -> {
            payment.makeCollect(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("The amount should be greater than 10 XAF", exception.getMessage());
    }

    @Test
    public void testMakeCollectWithSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeCollect(new HashMap<String, Object>() {{
                put("amount", 100);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", RandomGenerator.nonce());
                put("trxID", "1");
            }});
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isTransactionSuccess());
            Assertions.assertEquals(response.status, "SUCCESS");
            Assertions.assertEquals(response.transaction.amount, 97);
            Assertions.assertEquals(response.transaction.fees, 3);
            Assertions.assertEquals(response.transaction.b_party, "237670000000");
            Assertions.assertEquals(response.transaction.country, "CM");
            Assertions.assertEquals(response.transaction.currency, "XAF");
            Assertions.assertEquals(response.transaction.reference, "1");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMakeCollectWithSuccessAndProducts() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            List<Map<String, Object>> products  = new ArrayList<>();
            products.add(new HashMap<String, Object>() {{
                put("id", "SKU001");
                put("name", "Sac a Main");
                put("category", "Sac");
            }});
            TransactionResponse response = payment.makeCollect(new HashMap<String, Object>() {{
                put("amount", 100);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", RandomGenerator.nonce());
                put("products", products);
                put("trxID", "1");
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
            }});
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isTransactionSuccess());
            Assertions.assertEquals(response.transaction.products.length, 1);
            Assertions.assertEquals(response.status, "SUCCESS");
            Assertions.assertEquals(response.transaction.amount, 97);
            Assertions.assertEquals(response.transaction.fees, 3);
            Assertions.assertEquals(response.transaction.b_party, "237670000000");
            Assertions.assertEquals(response.transaction.country, "CM");
            Assertions.assertEquals(response.transaction.currency, "XAF");
            Assertions.assertEquals(response.transaction.reference, "1");
            Assertions.assertEquals(response.transaction.customer.phone, "+237677550439");
            Assertions.assertEquals(response.transaction.customer.email, "fisher.bank@gmail.com");
            Assertions.assertEquals(response.transaction.customer.first_name, "Fisher");
            Assertions.assertEquals(response.transaction.customer.last_name, "BANK");
            Assertions.assertEquals(response.transaction.location.town, "Douala");
            Assertions.assertEquals(response.transaction.location.country, "Cameroun");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMakeCollectWithPending() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeCollect(new HashMap<String, Object>() {{
                put("amount", 100);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", RandomGenerator.nonce());
                put("mode", "asynchronous");
            }});
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertFalse(response.isTransactionSuccess());
            Assertions.assertEquals(response.transaction.status, "PENDING");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMakeDepositWithServiceNotFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.makeDeposit(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("receiver", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testMakeDepositWithPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.makeDeposit(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("receiver", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testMakeDepositWithInvalidAmount() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        Exception exception = assertThrows(InvalidClientRequestException.class, () -> {
            payment.makeDeposit(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("receiver", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("The amount should be greater than 10 XAF", exception.getMessage());
    }

    @Test
    public void testMakeDepositWithSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeDeposit(new HashMap<String, Object>() {{
                put("amount", 100);
                put("service", "MTN");
                put("receiver", "670000000");
                put("nonce", RandomGenerator.nonce());
                put("trxID", "1");
            }});
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isTransactionSuccess());
            Assertions.assertEquals(response.status, "SUCCESS");
            Assertions.assertEquals(response.transaction.amount, 100);
            Assertions.assertEquals(response.transaction.fees, 0);
            Assertions.assertEquals(response.transaction.b_party, "237670000000");
            Assertions.assertEquals(response.transaction.country, "CM");
            Assertions.assertEquals(response.transaction.currency, "XAF");
            Assertions.assertEquals(response.transaction.reference, "1");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUnSetWhitelistIPs() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            Application response = payment.updateSecurity("whitelist_ips", "UNSET");
            Assertions.assertNull(response.getSecurityField("whitelist_ips"));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUnSetBlacklistReceivers() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            Application response = payment.updateSecurity("blacklist_receivers", "UNSET");
            Assertions.assertNull(response.getSecurityField("blacklist_receivers"));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetStatusNotServiceFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, payment::getStatus);
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testGetStatusPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, payment::getStatus);
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testGetStatusSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            Application application = payment.getStatus();
            Assertions.assertEquals("Meudocta Shop", application.name);
            Assertions.assertArrayEquals(new String[]{"CM", "NE"}, application.countries);
        } catch (ServerException | ServiceNotFoundException | PermissionDeniedException | IOException |
                 NoSuchAlgorithmException | InvalidClientRequestException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetTransactionsNotServiceFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.getTransactions(new String[]{"c6c40b76-8119-4e93-81bf-bfb55417b392"});
        });
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testGetTransactionsPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.getTransactions(new String[]{"c6c40b76-8119-4e93-81bf-bfb55417b392"});
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testGetTransactionsSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            Transaction[] transactions = payment.getTransactions(new String[]{"9886f099-dee2-4eaa-9039-e92b2ee33353"});
            Assertions.assertEquals(1, transactions.length);
            Assertions.assertEquals("9886f099-dee2-4eaa-9039-e92b2ee33353", transactions[0].pk);
        } catch (ServerException | ServiceNotFoundException | PermissionDeniedException | IOException |
                 NoSuchAlgorithmException | InvalidClientRequestException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCheckTransactionsNotServiceFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.checkTransactions(new String[]{"c6c40b76-8119-4e93-81bf-bfb55417b392"});
        });
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testCheckTransactionsPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.checkTransactions(new String[]{"c6c40b76-8119-4e93-81bf-bfb55417b392"});
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testCheckTransactionsSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            Transaction[] transactions = payment.checkTransactions(new String[]{"9886f099-dee2-4eaa-9039-e92b2ee33353"});
            Assertions.assertEquals(1, transactions.length);
            Assertions.assertEquals("9886f099-dee2-4eaa-9039-e92b2ee33353", transactions[0].pk);
        } catch (ServerException | ServiceNotFoundException | PermissionDeniedException | IOException |
                 NoSuchAlgorithmException | InvalidClientRequestException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
