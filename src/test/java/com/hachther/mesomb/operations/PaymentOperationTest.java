package com.hachther.mesomb.operations;

import com.hachther.mesomb.MeSomb;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentOperationTest {
    private final String applicationKey = "2bb525516ff374bb52545bf22ae4da7d655ba9fd";
    private final String accessKey = "c6c40b76-8119-4e93-81bf-bfb55417b392";
    private final String secretKey = "fe8c2445-810f-4caa-95c9-778d51580163";

    @BeforeEach
    public void onSetup() {
        MeSomb.apiBase = "http://192.168.8.102:8000";
    }

    @Test
    public void testMakeCollectWithServiceNotFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.makeCollect(5, "MTN", "677550203", new Date(), "fihser");
        });
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testMakeCollectWithPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.makeCollect(5, "MTN", "677550203", new Date(), "fihser");
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testMakeCollectWithInvalidAmount() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        Exception exception = assertThrows(InvalidClientRequestException.class, () -> {
            payment.makeCollect(5, "MTN", "677550203", new Date(), "fihser");
        });
        Assertions.assertEquals("The amount should be greater than 10 XAF", exception.getMessage());
    }

    @Test
    public void testMakeCollectWithSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeCollect(100, "MTN", "677550203", new Date(), RandomGenerator.nonce());
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isTransactionSuccess());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMakeCollectWithPending() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeCollect(100, "MTN", "677550203", new Date(), RandomGenerator.nonce(), "CM", "XAF", true, "asynchronous");
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertFalse(response.isTransactionSuccess());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException | InvalidClientRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMakeDepositWithServiceNotFound() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.makeDeposit(5, "MTN", "677550203", new Date(), "fihser");
        });
        Assertions.assertEquals("Application not found", exception.getMessage());
    }

    @Test
    public void testMakeDepositWithPermissionDenied() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.makeDeposit(5, "MTN", "677550203", new Date(), "fihser");
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testMakeDepositWithInvalidAmount() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        Exception exception = assertThrows(InvalidClientRequestException.class, () -> {
            payment.makeDeposit(5, "MTN", "677550203", new Date(), "fihser");
        });
        Assertions.assertEquals("The amount should be greater than 10 XAF", exception.getMessage());
    }

    @Test
    public void testMakeDepositWithSuccess() {
        PaymentOperation payment = new PaymentOperation(this.applicationKey, this.accessKey, this.secretKey);
        try {
            TransactionResponse response = payment.makeDeposit(100, "MTN", "677550203", new Date(), RandomGenerator.nonce());
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isTransactionSuccess());
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
            Assertions.assertEquals("Meudocta Shop", application.getName());
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
            JSONArray transactions = payment.getTransactions(new String[]{"9886f099-dee2-4eaa-9039-e92b2ee33353"});
            Assertions.assertEquals(1, transactions.size());
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
            JSONArray transactions = payment.checkTransactions(new String[]{"9886f099-dee2-4eaa-9039-e92b2ee33353"});
            Assertions.assertEquals(1, transactions.size());
        } catch (ServerException | ServiceNotFoundException | PermissionDeniedException | IOException |
                 NoSuchAlgorithmException | InvalidClientRequestException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
