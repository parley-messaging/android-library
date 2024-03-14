package nu.parley;

import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generation based on the documentation: https://developers.parley.nu/docs/authorization-header
 *
 * <b>NOTE</b>: It is important you generate the authHeader in a safe and secure location (not on a client device).
 * This is intended as an example inside the demo app to show the use of registered customers in Parley.
 * Especially the `parleySharedSecret` should <b>not exist anywhere</b> on the client device.
 */
public final class ParleyCustomerAuthorization {

    private static final String APPLICATION_AUTHORIZATION_SECRET = "fe4d1ba9-1428-45ae-b8e9-6859a2782776";

    /** @noinspection UnnecessaryLocalVariable*/
    public String generate(String customerId, String parleySharedSecret) throws InvalidKeyException, NoSuchAlgorithmException {

        // 1: CustomerAuthenticationKey
        String customerAuthenticationKey = encryptSHA512(customerId, APPLICATION_AUTHORIZATION_SECRET);

        // 2: VerifyHash
        long validateUntilTimestamp = (new Date().getTime() / 1000) + 60 * 60 * 24 * 7;
        String verifyHashData = customerId + customerAuthenticationKey + validateUntilTimestamp;
        String verifyHash = encryptSHA512(verifyHashData, parleySharedSecret);

        // 3: Authentication key
        String authenticationKeyData = customerId + "|" + customerAuthenticationKey + "|" + validateUntilTimestamp + "|" + verifyHash;
        String authenticationKey = encodeBase64(authenticationKeyData);

        return authenticationKey;
    }

    /**
     * Encrypts a string with SHA512 encryption with a specific key.
     *
     * @param value The value to encryptSHA512.
     * @param key   The secret key to use for the encryption.
     * @return The result after encrypting the string.
     * @noinspection SpellCheckingInspection
     */
    private String encryptSHA512(String value, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA512 = Mac.getInstance("HmacSHA512");

        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        hmacSHA512.init(secret_key);

        byte[] encryptedData = hmacSHA512.doFinal(value.getBytes());
        return String.format("%0128x", new BigInteger(1, encryptedData));
    }

    /**
     * Converts a given value to a Base64 encoded string.
     *
     * @param value The value to encode.
     * @return The base 64 encoded string.
     */
    private String encodeBase64(String value) {
        return Base64.encodeToString(value.getBytes(), Base64.NO_WRAP);
    }
}
