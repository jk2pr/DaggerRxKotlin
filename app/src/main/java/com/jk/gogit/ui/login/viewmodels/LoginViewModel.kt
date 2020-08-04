package com.jk.gogit.ui.login.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jk.gogit.ui.login.data.response.AccessToken
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.login.services.LoginExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LoginViewModel @ViewModelInject constructor(
        private val loginExecutor: LoginExecutor,
        @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _finalMutableLiveData = MutableLiveData<Resource<AccessToken>>()

    val finalDataLiveData: LiveData<Resource<AccessToken>>
        get() = _finalMutableLiveData

    @ExperimentalCoroutinesApi
    fun setState(mainState: MainState) {
        viewModelScope.launch {
            when (mainState) {
                is MainState.LoginEvent -> {
                    loginExecutor.execute()
                            .onEach {
                                _finalMutableLiveData.value = it
                            }.launchIn(viewModelScope)
                }
            }
        }
    }

    sealed class MainState {

        object LoginEvent : MainState()

        object None : MainState()
    }


}