package com.jk.gogit.ui.feed.services

import com.jk.gogit.model.Feed
import com.jk.gogit.network.api.IApi
import com.jk.gogit.ui.view.BaseActivity
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

    suspend fun execute(): Flow<List<Feed>> = flow {
        val userProfile = iApi.getMyProfile()
        val feeds = iApi.getFeed(userProfile.login, 1, BaseActivity.sMaxRecord)
        emit(feeds)


    }

}