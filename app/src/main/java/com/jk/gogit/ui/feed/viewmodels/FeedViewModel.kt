package com.jk.gogit.ui.feed.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jk.gogit.model.Feed
import com.jk.gogit.model.UserProfile
import com.jk.gogit.ui.feed.services.FeedExecutor
import com.jk.gogit.ui.login.data.response.AccessToken
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.login.services.LoginExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class FeedViewModel @ViewModelInject constructor(
        private val feedExecutor: FeedExecutor,
        @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _finalMutableLiveData = MutableLiveData<Resource<List<Feed>>>()

    val feedDataLiveData: LiveData<Resource<List<Feed>>>
        get() = _finalMutableLiveData

    @ExperimentalCoroutinesApi
    fun setState(mainState: MainState) {
        viewModelScope.launch {
            when (mainState) {
                is MainState.FeedEvent -> {
                    feedExecutor.execute()
                            .onEach {
                                _finalMutableLiveData.value = it
                            }.catch { e ->
                                print("Error $e")
                            }.collect{

                            }
                }
            }
        }
    }

     sealed class MainState {

        object FeedEvent : MainState()

        object None : MainState()
    }


}