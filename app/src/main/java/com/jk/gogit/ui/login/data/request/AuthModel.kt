package com.jk.gogit.ui.login.data.request

import com.google.gson.annotations.SerializedName
import com.jk.gogit.BuildConfig

public class AuthRequestModel {

    private var scopes: List<String>? = null
    private var note: String? = null
    private var noteUrl: String? = null
    @SerializedName("client_id")
    private var clientId: String? = null
    @SerializedName("client_secret")
    private var clientSecret: String? = null

    fun generate(): AuthRequestModel {
        val model = AuthRequestModel()
        model.scopes = listOf("user", "repo", "gist", "notifications")
        model.note = BuildConfig.APPLICATION_ID
        model.clientId = BuildConfig.client_id
        model.clientSecret = BuildConfig.client_secret
        model.noteUrl = "https://gogit-5a346.firebaseapp.com/__/auth/handler"
        return model
    }
}