package com.hachther.mesomb.operations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hachther.mesomb.MeSomb;
import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.Contribution;
import com.hachther.mesomb.models.ContributionResponse;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class FundraisingOperationTest {
    private final String fundKey = "fa78bded201b791712ee398c7ddfb8652669404f";
    private final String accessKey = "c6c40b76-8119-4e93-81bf-bfb55417b392";
    private final String secretKey = "fe8c2445-810f-4caa-95c9-778d51580163";

    @BeforeEach
    public void onSetup() {
        MeSomb.apiBase = "http://127.0.0.1:8000";
        MeSomb.requestTimeout = 60;
    }

    @Test
    public void testMakeContributeWithServiceNotFound() {
        FundraisingOperation payment = new FundraisingOperation(this.fundKey + "f", this.accessKey, this.secretKey);
        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            payment.makeContribution(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("Fund not found", exception.getMessage());
    }

    @Test
    public void testMakeContributeWithPermissionDenied() {
        FundraisingOperation payment = new FundraisingOperation(this.fundKey, this.accessKey + "f", this.secretKey);
        Exception exception = assertThrows(PermissionDeniedException.class, () -> {
            payment.makeContribution(new HashMap<String, Object>() {{
                put("amount", 5);
                put("service", "MTN");
                put("payer", "670000000");
                put("nonce", "fihser");
            }});
        });
        Assertions.assertEquals("Invalid access key", exception.getMessage());
    }

    @Test
    public void testMakeContributeWithSuccess() {
        FundraisingOperation payment = new FundraisingOperation(this.fundKey, this.accessKey, this.secretKey);
        try {
            ContributionResponse response = payment.makeContribution(new HashMap<String, Object>() {{
                put("amount", 100);
                put("service", "MTN");
                put("payer", "670000000");
                put("trxID", "1");
                put("full_name", new HashMap<String, String>() {{
                    put("first_name", "John");
                    put("last_name", "Doe");
                }});
                put("contact", new HashMap<String, String>() {{
                    put("email", "contact@gmail.com");
                    put("phone_number", "+237677550203");
                }});
            }});
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isContributionSuccess());
            Assertions.assertEquals(response.status, "SUCCESS");
            Assertions.assertEquals(response.contribution.amount, 98);
            Assertions.assertEquals(response.contribution.fees, 2);
            Assertions.assertEquals(response.contribution.b_party, "237670000000");
            Assertions.assertEquals(response.contribution.country, "CM");
            Assertions.assertEquals(response.contribution.currency, "XAF");
            Assertions.assertEquals(response.contribution.reference, "1");
            Assertions.assertEquals(response.contribution.service, "MTN");
            Assertions.assertEquals(response.contribution.contributor.email, "contact@gmail.com");
            Assertions.assertEquals(response.contribution.contributor.phone, "+237677550203");
            Assertions.assertEquals(response.contribution.contributor.firstName, "John");
            Assertions.assertEquals(response.contribution.contributor.lastName, "Doe");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException |
                 InvalidClientRequestException | ParseException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMakeContributeWithSuccessWithAnonymous() {
        FundraisingOperation payment = new FundraisingOperation(this.fundKey, this.accessKey, this.secretKey);
        try {
            ContributionResponse response = payment.makeContribution(new HashMap<String, Object>() {{
                put("amount", 100);
                put("service", "MTN");
                put("payer", "670000000");
                put("trxID", "1");
                put("anonymous", true);
            }});
            Assertions.assertTrue(response.isOperationSuccess());
            Assertions.assertTrue(response.isContributionSuccess());
            Assertions.assertEquals(response.status, "SUCCESS");
            Assertions.assertEquals(response.contribution.amount, 98);
            Assertions.assertEquals(response.contribution.fees, 2);
            Assertions.assertEquals(response.contribution.b_party, "237670000000");
            Assertions.assertEquals(response.contribution.country, "CM");
            Assertions.assertEquals(response.contribution.currency, "XAF");
            Assertions.assertEquals(response.contribution.reference, "1");
            Assertions.assertEquals(response.contribution.service, "MTN");
            Assertions.assertNull(response.contribution.contributor);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ServiceNotFoundException | PermissionDeniedException |
                 InvalidClientRequestException | ParseException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetTransactionsSuccess() {
        FundraisingOperation payment = new FundraisingOperation(this.fundKey, this.accessKey, this.secretKey);
        try {
            Contribution[] transactions = payment.getContributions(new String[]{"0685831f-4145-4352-ae81-155fec42c748"});
            Assertions.assertEquals(1, transactions.length);
            Assertions.assertEquals("0685831f-4145-4352-ae81-155fec42c748", transactions[0].pk);
        } catch (ServerException | ServiceNotFoundException | PermissionDeniedException |
                 IOException | NoSuchAlgorithmException | InvalidClientRequestException |
                 InvalidKeyException | ParseException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCheckTransactionsSuccess() {
        FundraisingOperation payment = new FundraisingOperation(this.fundKey, this.accessKey, this.secretKey);
        try {
            Contribution[] transactions = payment.getContributions(new String[]{"0685831f-4145-4352-ae81-155fec42c748"});
            Assertions.assertEquals(1, transactions.length);
            Assertions.assertEquals("0685831f-4145-4352-ae81-155fec42c748", transactions[0].pk);
        } catch (ServerException | ServiceNotFoundException | PermissionDeniedException |
                 IOException | NoSuchAlgorithmException | InvalidClientRequestException |
                 InvalidKeyException | ParseException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
