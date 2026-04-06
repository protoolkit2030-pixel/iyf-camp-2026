package com.iyf.camp2026.data.remote

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.iyf.camp2026.domain.model.Inscription
import com.iyf.camp2026.utils.JwtHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val TAG = "GoogleSheetsService"
        private const val SPREADSHEET_ID = "1pm9vCmy4TAoa7EQqMRFlyYu01GahFN6OeAMOHecQnHs"
        private const val SHEET_NAME = "Inscriptions_Camp_IYF_2026"
        private const val SHEETS_SCOPE = "https://www.googleapis.com/auth/spreadsheets"
        private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
    }

    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0L

    private fun loadCredentials(): ServiceAccountCredentials {
        val credentialsJson = context.assets.open("credentials.json").bufferedReader().use { it.readText() }
        val json = JSONObject(credentialsJson)
        return ServiceAccountCredentials(
            type = json.getString("type"),
            projectId = json.getString("project_id"),
            privateKeyId = json.getString("private_key_id"),
            privateKey = json.getString("private_key"),
            clientEmail = json.getString("client_email"),
            clientId = json.getString("client_id"),
            authUri = json.getString("auth_uri"),
            tokenUri = json.getString("token_uri")
        )
    }

    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        // Return cached token if still valid (5 min buffer)
        val now = System.currentTimeMillis() / 1000
        if (cachedToken != null && now < tokenExpiry - 300) {
            return@withContext cachedToken!!
        }

        val credentials = loadCredentials()
        val jwt = JwtHelper.createJwt(
            clientEmail = credentials.clientEmail,
            privateKeyPem = credentials.privateKey,
            scope = SHEETS_SCOPE
        )

        val formBody = FormBody.Builder()
            .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
            .add("assertion", jwt)
            .build()

        val request = Request.Builder()
            .url(TOKEN_URL)
            .post(formBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Réponse vide du serveur d'authentification")

        if (!response.isSuccessful) {
            Log.e(TAG, "Erreur token: $responseBody")
            throw Exception("Erreur d'authentification Google: ${response.code}")
        }

        val tokenJson = JSONObject(responseBody)
        cachedToken = tokenJson.getString("access_token")
        tokenExpiry = now + tokenJson.getInt("expires_in")

        cachedToken!!
    }

    suspend fun appendInscription(inscription: Inscription): Boolean = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessToken()

            // Build the row values matching the sheet columns:
            // A - N° Référence | B - Date & Heure | C - Nom | D - Prénom |
            // E - Date de naissance | F - Sexe | G - Téléphone | H - Email |
            // I - Quartier/Commune | J - Établissement | K - Classe/Niveau |
            // L - Filière | M - Cours sélectionnés | N - Montant (FCFA) |
            // O - Mode de paiement | P - N° Transaction Mobile Money | Q - Statut

            val rowValues = listOf(
                inscription.referenceNumber,
                inscription.dateHeure,
                inscription.nom,
                inscription.prenom,
                inscription.dateNaissance,
                inscription.sexe,
                inscription.telephone,
                inscription.email,
                inscription.quartier,
                inscription.etablissement,
                inscription.classe,
                inscription.filiere,
                inscription.coursSelectionnesString,
                "${inscription.montant} FCFA",
                inscription.modePaiement,
                inscription.numeroTransaction,
                inscription.statut
            )

            val requestBody = JSONObject().apply {
                put("values", JSONArray().apply {
                    put(JSONArray(rowValues))
                })
            }

            val range = "$SHEET_NAME!A:Q"
            val url = "https://sheets.googleapis.com/v4/spreadsheets/$SPREADSHEET_ID/values/$range:append?valueInputOption=USER_ENTERED&insertDataOption=INSERT_ROWS"

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                Log.d(TAG, "Inscription envoyée avec succès: ${inscription.referenceNumber}")
                true
            } else {
                Log.e(TAG, "Erreur envoi: ${response.code} - $responseBody")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception lors de l'envoi vers Google Sheets", e)
            false
        }
    }
}
