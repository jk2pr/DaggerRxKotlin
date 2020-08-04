package com.jk.gogit.network.api

import com.jk.gogit.ui.login.data.response.AccessToken
import com.jk.gogit.ui.login.data.request.AuthRequestModel
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ILogin {
    @POST("authorizations")
    @Headers("Accept: application/json")
    suspend fun authorizations(
            @Body authRequestModel: AuthRequestModel
    ): AccessToken
}