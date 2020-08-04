package com.jk.gogit.ui.login.services

import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import com.jk.gogit.R
import com.jk.gogit.exception.UserUnAuthorizedException
import com.jk.gogit.model.UserProfile
import com.jk.gogit.network.api.IApi
import com.jk.gogit.ui.login.data.response.AccessToken
import com.jk.gogit.ui.login.data.request.AuthRequestModel
import com.jk.gogit.network.api.ILogin
import com.jk.gogit.ui.login.data.response.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class LoginExecutor
@Inject constructor(private val iLoginApi: ILogin, private val iApi: IApi, val mAuth: FirebaseAuth) {

    /**
     * Show loading
     * Get Data from network
     * Show FinalData
     */

    suspend fun execute(): Flow<Resource<AccessToken>> = flow {
        emit(Resource.Loading)

        try {
            val loginData = iLoginApi.authorizations(AuthRequestModel().generate())
            val firebaseUser = signInWithToken(loginData.token)
            if (firebaseUser == null)
                emit(Resource.Error(Exception("Error sign in with FireBase")))
            else
                emit(Resource.Success(loginData))
        } catch (e: UserUnAuthorizedException) {
            emit(Resource.Error(e))
        }


    }

    private suspend fun signInWithToken(token: String): FirebaseUser? {
        val credential = GithubAuthProvider.getCredential(token)
        val authResult = mAuth.signInWithCredential(credential).await()
        /* .addOnCompleteListener { task ->
             /// debug("signInWithCredential:onComplete:" + task.isSuccessful)
             if (!task.isSuccessful) {
                 task.exception?.printStackTrace()
             } else {
                // pref.edit().putString("AccessToken", token).apply()
                 //val user = task.result?.user
                 // NavUtils.redirectToHome(requireActivity(), user)

             }
         }*/
        return authResult.user
    }

}