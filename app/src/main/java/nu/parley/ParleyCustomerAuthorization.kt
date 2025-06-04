package nu.parley

import android.util.Base64
import java.math.BigInteger
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Date
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Generation based on the documentation: https://developers.parley.nu/docs/authorization-header
 *
 * **NOTE**: It is important you generate the authHeader in a safe and secure location (not on a client device).
 * This is intended as an example inside the demo app to show the use of registered customers in Parley.
 * Especially the `parleySharedSecret` should **not exist anywhere** on the client device.
 */
class ParleyCustomerAuthorization {

    companion object {
        private const val APPLICATION_AUTHORIZATION_SECRET = "fe4d1ba9-1428-45ae-b8e9-6859a2782776"
    }

    /** @noinspection UnnecessaryLocalVariable
     */
    @Throws(InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun generate(customerId: String, parleySharedSecret: String): String {
        // 1: CustomerAuthenticationKey

        val customerAuthenticationKey = encryptSHA512(customerId, APPLICATION_AUTHORIZATION_SECRET)

        // 2: VerifyHash
        val validateUntilTimestamp = (Date().time / 1000) + 60 * 60 * 24 * 7
        val verifyHashData = customerId + customerAuthenticationKey + validateUntilTimestamp
        val verifyHash = encryptSHA512(verifyHashData, parleySharedSecret)

        // 3: Authentication key
        val authenticationKeyData =
            "$customerId|$customerAuthenticationKey|$validateUntilTimestamp|$verifyHash"
        val authenticationKey = encodeBase64(authenticationKeyData)

        return authenticationKey
    }

    /**
     * Encrypts a string with SHA512 encryption with a specific key.
     *
     * @param value The value to encryptSHA512.
     * @param key   The secret key to use for the encryption.
     * @return The result after encrypting the string.
     * @noinspection SpellCheckingInspection
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun encryptSHA512(value: String, key: String): String {
        val hmacSHA512 = Mac.getInstance("HmacSHA512")

        val secret_key = SecretKeySpec(key.toByteArray(), "HmacSHA512")
        hmacSHA512.init(secret_key)

        val encryptedData = hmacSHA512.doFinal(value.toByteArray())
        return String.format("%0128x", BigInteger(1, encryptedData))
    }

    /**
     * Converts a given value to a Base64 encoded string.
     *
     * @param value The value to encode.
     * @return The base 64 encoded string.
     */
    private fun encodeBase64(value: String): String {
        return Base64.encodeToString(value.toByteArray(), Base64.NO_WRAP)
    }
}
