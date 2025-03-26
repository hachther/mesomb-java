package com.hachther.mesomb.operations;

import com.hachther.mesomb.exceptions.InvalidClientRequestException;
import com.hachther.mesomb.exceptions.PermissionDeniedException;
import com.hachther.mesomb.exceptions.ServerException;
import com.hachther.mesomb.exceptions.ServiceNotFoundException;
import com.hachther.mesomb.models.PaginatedWalletTransactions;
import com.hachther.mesomb.models.PaginatedWallets;
import com.hachther.mesomb.models.Wallet;
import com.hachther.mesomb.models.WalletTransaction;
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

public class WalletOperation extends AOperation {
    public WalletOperation(String providerKey, String accessKey, String secretKey, String language) {
        super(providerKey, accessKey, secretKey, language);
    }

    public WalletOperation(String providerKey, String accessKey, String secretKey) {
        super(providerKey, accessKey, secretKey, "en");
    }

    /**
     * Create a wallet
     *
     *
     * @param params Map<String, Object> containing the following parameters:
     *               - first_name of the wallet owner (optional)
     *               - last_name of the wallet owner
     *               - email of the wallet owner (optional)
     *               - phone_number of the wallet owner
     *               - country of the wallet owner (default CM)
     *               - gender of the wallet owner
     *               - nonce (optional)
     *               - number The unique numeric wallet identifier, if not set we will generate one for you (optional)
     *
     * @return Wallet
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public Wallet createWallet(Map<String, Object> params) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException {
        String endpoint = "wallet/wallets/";

        String nonce = RandomGenerator.nonce();

        if (params.containsKey("nonce")) {
            nonce = (String) params.remove("nonce");
        }

        JSONParser parser = new JSONParser();
        return new Wallet((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), nonce, params)));
    }

    /**
     * Get a wallet
     *
     * @param id The wallet identifier
     *
     * @return Wallet
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public Wallet getWallet(Long id) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException {
        String endpoint = "wallet/wallets/" + id + "/";

        JSONParser parser = new JSONParser();
        return new Wallet((JSONObject) parser.parse(this.executeRequest("GET", endpoint, new Date(), RandomGenerator.nonce(), null)));
    }

    /**
     * Get wallets
     *
     * @param page The page number
     *
     * @return PaginatedWallets
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public PaginatedWallets getWallets(int page) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException {
        String endpoint = "wallet/wallets/?page=" + page;

        JSONParser parser = new JSONParser();
        return new PaginatedWallets((JSONObject) parser.parse(this.executeRequest("GET", endpoint, new Date(), RandomGenerator.nonce(), null)));
    }

    /**
     * Update a wallet
     * @param id The wallet identifier
     * @param params Map<String, Object> containing the following parameters:
     *               - first_name of the wallet owner (optional)
     *               - last_name of the wallet owner
     *               - email of the wallet owner (optional)
     *               - phone_number of the wallet owner
     *               - country of the wallet owner (default CM)
     *               - gender of the wallet owner
     *               - nonce (optional)
     *               - number The unique numeric wallet identifier, if not set we will generate one for you (optional)
     *
     * @return Wallet
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public Wallet updateWallet(Long id, Map<String, Object> params) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException {
        String endpoint = "wallet/wallets/" + id + "/";

        String nonce = RandomGenerator.nonce();

        if (params.containsKey("nonce")) {
            nonce = (String) params.remove("nonce");
        }

        JSONParser parser = new JSONParser();
        return new Wallet((JSONObject) parser.parse(this.executeRequest("PUT", endpoint, new Date(), nonce, params)));
    }

    /**
     *
     * @param id The wallet identifier
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public void deleteWallet(Long id) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException {
        String endpoint = "wallet/wallets/" + id + "/";

        this.executeRequest("DELETE", endpoint, new Date(), RandomGenerator.nonce(), null);
    }

    /**
     * Remove money to a wallet
     *
     * @param wallet The wallet identifier
     * @param amount The amount to add
     * @param force Force the operation if balance is not enough
     * @param message The message to add to the transaction (optional)
     * @param externalId The external identifier of the transaction (optional)
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction removeMoney(Long wallet, float amount, boolean force, String message, String externalId) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        String endpoint = "wallet/wallets/" + wallet + "/adjust/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("force", force);
        body.put("direction", -1);

        if (message != null) {
            body.put("message", message);
        }

        if (externalId != null) {
            body.put("trxID", externalId);
        }

        JSONParser parser = new JSONParser();
        return new WalletTransaction((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), RandomGenerator.nonce(), body)));
    }

    /**
     * Remove money to a wallet
     *
     * @param wallet The wallet identifier
     * @param amount The amount to add
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction removeMoney(Long wallet, float amount) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return removeMoney(wallet, amount, false, null, null);
    }

    /**
     * Add money to a wallet
     *
     * @param wallet The wallet identifier
     * @param amount The amount to add
     * @param message The message to add to the transaction (optional)
     * @param externalId The external identifier of the transaction (optional)
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction addMoney(Long wallet, float amount, String message, String externalId) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        String endpoint = "wallet/wallets/" + wallet + "/adjust/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("direction", 1);

        if (message != null) {
            body.put("message", message);
        }

        if (externalId != null) {
            body.put("trxID", externalId);
        }

        JSONParser parser = new JSONParser();
        return new WalletTransaction((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), RandomGenerator.nonce(), body)));
    }

    /**
     * Add money to a wallet
     *
     * @param wallet The wallet identifier
     * @param amount The amount to add
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction addMoney(Long wallet, float amount) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return addMoney(wallet, amount, null, null);
    }

    /**
     * Remove money to a wallet
     *
     * @param from The wallet identifier
     * @param to The wallet identifier
     * @param amount The amount to add
     * @param message The message to add to the transaction (optional)
     * @param externalId The external identifier of the transaction (optional)
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction transferMoney(Long from, Long to, float amount, boolean force, String message, String externalId) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        String endpoint = "wallet/wallets/" + from + "/transfer/";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("to", to);
        body.put("force", force);

        if (message != null) {
            body.put("message", message);
        }

        if (externalId != null) {
            body.put("trxID", externalId);
        }

        JSONParser parser = new JSONParser();
        return new WalletTransaction((JSONObject) parser.parse(this.executeRequest("POST", endpoint, new Date(), RandomGenerator.nonce(), body)));
    }

    /**
     * Remove money to a wallet
     *
     * @param from The wallet identifier
     * @param to The wallet identifier
     * @param amount The amount to add
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction transferMoney(Long from, Long to, float amount, boolean force) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return transferMoney(from, to, amount, force, null, null);
    }

    /**
     * Get transactions
     *
     * @param page The page number
     * @param wallet The wallet identifier
     *
     * @return PaginatedWallets
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public PaginatedWalletTransactions listTransactions(int page, Long wallet) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        String endpoint = "wallet/transactions/?page=" + page;
        if (wallet != null) {
            endpoint += "&wallet=" + wallet;
        }

        JSONParser parser = new JSONParser();
        return new PaginatedWalletTransactions((JSONObject) parser.parse(this.executeRequest("GET", endpoint, new Date(), RandomGenerator.nonce(), null)));
    }

    /**
     * Get transactions
     *
     * @param page The page number
     *
     * @return PaginatedWallets
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public PaginatedWalletTransactions listTransactions(int page) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        return listTransactions(page, null);
    }

    /**
     * Get transactions
     *
     * @param id The transaction identifier
     *
     * @return WalletTransaction
     *
     * @throws ServerException if an error occurred on the server
     * @throws ServiceNotFoundException if the service is not found
     * @throws PermissionDeniedException if the permission is denied
     * @throws IOException if an error occurred while reading the response
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidClientRequestException if the request is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public WalletTransaction getTransaction(Long id) throws ServerException, ServiceNotFoundException, PermissionDeniedException, IOException, NoSuchAlgorithmException, InvalidClientRequestException, InvalidKeyException, ParseException, java.text.ParseException {
        String endpoint = "wallet/transactions/" + id + "/";

        JSONParser parser = new JSONParser();
        return new WalletTransaction((JSONObject) parser.parse(this.executeRequest("GET", endpoint, new Date(), RandomGenerator.nonce(), null)));
    }

    public WalletTransaction[] getTransactions(String[] ids, String source) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        String[] query = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            query[i] = "ids=" + ids[i];
        }

        String endpoint = "wallet/transactions/search/?" + String.join("&", query) + "&source=" + source;

        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(this.executeRequest("GET", endpoint, new Date()));
        WalletTransaction[] transactions = new WalletTransaction[response.size()];
        for (int i = 0; i < response.size(); i++) {
            transactions[i] = new WalletTransaction((JSONObject) response.get(i));
        }
        return transactions;
    }

    public WalletTransaction[] getTransactions(String[] ids) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, ServiceNotFoundException, PermissionDeniedException, InvalidClientRequestException, ParseException, java.text.ParseException {
        return getTransactions(ids, "MESOMB");
    }

    @Override
    public String getService() {
        return "wallet";
    }
}
