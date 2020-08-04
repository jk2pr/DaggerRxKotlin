package com.jk.gogit.ui.login.data.response

import com.google.gson.annotations.SerializedName

data class AccessToken(
        @SerializedName("id")
        val id: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("note")
        val note: String,
        @SerializedName("created_at")
        val created_at: String,
        @SerializedName("message")
        val message: String)