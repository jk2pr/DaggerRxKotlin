package com.jk.gogit.ui.feed.services

import com.jk.gogit.exception.UserUnAuthorizedException
import com.jk.gogit.model.Feed
import com.jk.gogit.model.UserProfile
import com.jk.gogit.network.api.IApi
import com.jk.gogit.ui.login.data.response.AccessToken
import com.jk.gogit.ui.login.data.request.AuthRequestModel
import com.jk.gogit.network.api.ILogin
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.view.BaseActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeedExecutor
@Inject constructor(private val iApi: IApi) {

    /**
     * Show loading
     * Get Data from network
     * Show FinalData
     */

    suspend fun execute(): Flow<Resource<List<Feed>>> = flow {
        emit(Resource.Loading)

        try {
            val userProfile = iApi.getMyProfile()
           val feeds = iApi.getFeed(userProfile.login, 1, BaseActivity.sMaxRecord)
            emit(Resource.Success(feeds))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }


    }

}