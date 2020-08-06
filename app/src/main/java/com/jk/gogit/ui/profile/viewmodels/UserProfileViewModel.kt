package com.jk.gogit.ui.profile.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jk.gogit.model.UserProfile
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.profile.services.UserProfileExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class UserProfileViewModel @ViewModelInject constructor(

        private val userProfileExecutor: UserProfileExecutor,
        @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val selectedUser = savedStateHandle.getLiveData<String>("id")
    private val _finalMutableLiveData = MutableLiveData<Resource<UserProfile>>()

    val finalDataLiveData: LiveData<Resource<UserProfile>>
        get() = _finalMutableLiveData

    fun setLoginId(id: String) {
        savedStateHandle.set("id", id)
    }

    @ExperimentalCoroutinesApi
    fun setState(mainState: MainState) {
        viewModelScope.launch {
            when (mainState) {
                is MainState.UserProfileEvent -> {

                    userProfileExecutor.execute(loginId = selectedUser.value!!)
                            .onEach {
                                _finalMutableLiveData.value = it
                            }.launchIn(viewModelScope)
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        print("UserProfileViewModel Cleared")
    }

    sealed class MainState {

        object UserProfileEvent : MainState()

        object None : MainState()
    }


}