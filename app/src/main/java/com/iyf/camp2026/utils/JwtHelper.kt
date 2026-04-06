package com.iyf.camp2026.utils

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

object JwtHelper {
    private const val TAG = "JwtHelper"

    /**
     * Creates a signed JWT for Google Service Account authentication.
     *
     * @param clientEmail The service account email
     * @param privateKeyPem The PEM-encoded private key from credentials.json
     * @param scope The Google API scope to request
     * @return A signed JWT string
     */
    fun createJwt(
        clientEmail: String,
        privateKeyPem: String,
        scope: String
    ): String {
        val now = System.currentTimeMillis() / 1000L
        val expiry = now + 3600L // 1 hour

        // Create JWT Header
        val headerJson = """{"alg":"RS256","typ":"JWT"}"""
        val headerEncoded = base64UrlEncode(headerJson.toByteArray(Charsets.UTF_8))

        // Create JWT Payload
        val payloadJson = """
            {
                "iss": "$clientEmail",
                "scope": "$scope",
                "aud": "https://oauth2.googleapis.com/token",
                "iat": $now,
                "exp": $expiry
            }
        """.trimIndent()
        val payloadEncoded = base64UrlEncode(payloadJson.toByteArray(Charsets.UTF_8))

        // Create signing input
        val signingInput = "$headerEncoded.$payloadEncoded"

        // Sign with RS256
        val privateKey = loadPrivateKey(privateKeyPem)
        val signature = Signature.getInstance("SHA256withRSA").apply {
            initSign(privateKey)
            update(signingInput.toByteArray(Charsets.UTF_8))
        }.sign()
        val signatureEncoded = base64UrlEncode(signature)

        val jwt = "$signingInput.$signatureEncoded"
        Log.d(TAG, "JWT créé pour: $clientEmail")
        return jwt
    }

    private fun base64UrlEncode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun loadPrivateKey(pem: String): java.security.PrivateKey {
        // Clean the PEM string - handle both \n (escaped) and actual newlines
        val cleanedPem = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .replace("\r", "")
            .replace(" ", "")
            .trim()

        val keyBytes = Base64.decode(cleanedPem, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }
}
