package com.hachther.mesomb.operations;

import com.hachther.mesomb.MeSomb;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.PaginatedWallets;
import com.hachther.mesomb.models.Wallet;
import com.hachther.mesomb.models.WalletTransaction;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class WalletOperationTest {
    private final String providerKey = "a1dc7a7391c538788043";
    private final String accessKey = "c6c40b76-8119-4e93-81bf-bfb55417b392";
    private final String secretKey = "fe8c2445-810f-4caa-95c9-778d51580163";

    @BeforeEach
    public void onSetup() {
        MeSomb.apiBase = "http://127.0.0.1:8000";
        MeSomb.requestTimeout = 60;
    }

    @Test
    public void testCreateWalletWithSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        Wallet wallet = client.createWallet(new HashMap<>() {{
            put("first_name", "John");
            put("last_name", "Doe");
            put("email", "contact@gmail.com");
            put("phone_number", "+237677550000");
            put("country", "CM");
            put("gender", "MAN");
        }});

        Assertions.assertEquals(wallet.firstName, "John");
        Assertions.assertEquals(wallet.lastName, "Doe");
        Assertions.assertEquals(wallet.email, "contact@gmail.com");
        Assertions.assertEquals(wallet.phoneNumber, "+237677550000");
        Assertions.assertEquals(wallet.country, "CM");
        Assertions.assertEquals(wallet.gender, "MAN");
        Assertions.assertNotNull(wallet.number);
    }

    @Test
    public void testCreateWalletWithMinValueSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        Wallet wallet = client.createWallet(new HashMap<>() {{
            put("last_name", "Doe");
            put("phone_number", "+237677550000");
            put("gender", "MAN");
        }});

        Assertions.assertEquals(wallet.lastName, "Doe");
        Assertions.assertEquals(wallet.phoneNumber, "+237677550000");
        Assertions.assertEquals(wallet.gender, "MAN");
        Assertions.assertNotNull(wallet.number);
    }

    @Test
    public void testUpdateWalletWithMinValueSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        Wallet wallet = client.updateWallet(229L, new HashMap<>() {{
            put("last_name", "Doe");
            put("phone_number", "+237677550002");
            put("gender", "WOMAN");
        }});

        Assertions.assertEquals(wallet.id, 229);
        Assertions.assertEquals(wallet.lastName, "Doe");
        Assertions.assertEquals(wallet.phoneNumber, "+237677550002");
        Assertions.assertEquals(wallet.gender, "WOMAN");
        Assertions.assertNotNull(wallet.number);
    }

    @Test
    public void testGetWalletWithSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        Wallet wallet = client.getWallet(228L);

        Assertions.assertEquals(wallet.id, 228);
        Assertions.assertEquals(wallet.firstName, "John");
        Assertions.assertEquals(wallet.lastName, "Doe");
        Assertions.assertEquals(wallet.email, "contact@gmail.com");
        Assertions.assertEquals(wallet.phoneNumber, "+237677550000");
        Assertions.assertEquals(wallet.country, "CM");
        Assertions.assertEquals(wallet.gender, "MAN");
        Assertions.assertNotNull(wallet.number);
    }

    @Test
    public void testGetWalletsWithSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        PaginatedWallets pagination = client.getWallets(1);
        Assertions.assertTrue(pagination.count > 0);
        Assertions.assertTrue(pagination.results.length > 0);
        Assertions.assertNull(pagination.previous);
    }

    @Test
    public void testAddMoneyToWalletWithSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException, java.text.ParseException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        Wallet wallet = client.getWallet(228L);
        WalletTransaction transaction = client.addMoney(228L, 10000);

        Assertions.assertEquals(transaction.direction, 1);
        Assertions.assertEquals(transaction.status, "SUCCESS");
        Assertions.assertEquals(transaction.amount, 10000);
        Assertions.assertEquals(transaction.balanceAfter, (wallet.balance != null ? wallet.balance : 0) + 10000);
        Assertions.assertEquals(transaction.wallet, 228);
        Assertions.assertEquals(transaction.country, "CM");
        Assertions.assertNotNull(transaction.finTrxId);
        Assertions.assertNotNull(transaction.date);
    }

    @Test
    public void testRemoveMoneyToWalletWithSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException, java.text.ParseException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        Wallet wallet = client.getWallet(228L);
        WalletTransaction transaction = client.removeMoney(228L, 10000);

        Assertions.assertEquals(transaction.direction, -1);
        Assertions.assertEquals(transaction.status, "SUCCESS");
        Assertions.assertEquals(transaction.amount, 10000);
        Assertions.assertEquals(transaction.balanceAfter, (wallet.balance != null ? wallet.balance : 0) - 10000);
        Assertions.assertEquals(transaction.wallet, 228);
        Assertions.assertEquals(transaction.country, "CM");
        Assertions.assertNotNull(transaction.finTrxId);
        Assertions.assertNotNull(transaction.date);
    }

    @Test
    public void testGetTransactionDetailWithSuccess() throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, ParseException, InvalidKeyException, java.text.ParseException {
        WalletOperation client = new WalletOperation(this.providerKey, this.accessKey, this.secretKey);
        WalletTransaction transaction = client.getTransaction(3061L);

        Assertions.assertEquals(transaction.id, 3061L);
        Assertions.assertEquals(transaction.direction, -1);
        Assertions.assertEquals(transaction.status, "SUCCESS");
        Assertions.assertEquals(transaction.amount, 1000);
        Assertions.assertEquals(transaction.balanceAfter, 1000);
        Assertions.assertEquals(transaction.wallet, 228);
        Assertions.assertEquals(transaction.country, "CM");
        Assertions.assertNotNull(transaction.finTrxId);
        Assertions.assertNotNull(transaction.date);
    }
}
