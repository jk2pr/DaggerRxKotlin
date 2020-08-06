package com.jk.gogit.ui.profile.services

import com.google.firebase.auth.FirebaseAuth
import com.jk.gogit.exception.UserUnAuthorizedException
import com.jk.gogit.model.UserProfile
import com.jk.gogit.network.api.IApi
import com.jk.gogit.ui.login.data.response.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserProfileExecutor
@Inject constructor(private val iApi: IApi, val mAuth: FirebaseAuth) {

    /**
     * Show loading
     * Get Data from network
     * Show FinalData
     */

    suspend fun execute(loginId: String): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading)

        try {
            val loginData = iApi.getUserProfile(loginId)
            emit(Resource.Success(loginData))
        } catch (e: UserUnAuthorizedException) {
            emit(Resource.Error(e))
        }


    }


}