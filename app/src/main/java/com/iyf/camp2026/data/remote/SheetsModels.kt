package com.iyf.camp2026.data.remote

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class AppendValuesRequest(
    val values: List<List<String>>
)

data class AppendValuesResponse(
    val spreadsheetId: String,
    val tableRange: String,
    val updates: UpdatesInfo?
)

data class UpdatesInfo(
    val spreadsheetId: String,
    val updatedRange: String,
    val updatedRows: Int,
    val updatedColumns: Int,
    val updatedCells: Int
)

data class ServiceAccountCredentials(
    val type: String,
    val projectId: String,
    val privateKeyId: String,
    val privateKey: String,
    val clientEmail: String,
    val clientId: String,
    val authUri: String,
    val tokenUri: String
)
