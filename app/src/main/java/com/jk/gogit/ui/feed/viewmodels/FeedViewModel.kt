package com.jk.gogit.ui.feed.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jk.gogit.ui.feed.services.FeedExecutor
import com.jk.gogit.ui.login.data.response.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart


@ExperimentalCoroutinesApi
class FeedViewModel @ViewModelInject constructor(
        private val _feedExecutor: FeedExecutor,
        @Assisted private val _savedStateHandle: SavedStateHandle) : ViewModel() {


    private val _action = MutableLiveData<Action>()

    init {
        setState(Action.Load)
    }

    @InternalCoroutinesApi
    val feedDataLiveData = _action.switchMap {
        liveData(viewModelScope.coroutineContext) {
            when (_action.value) {
                is Action.Load -> {
                    _feedExecutor.execute()
                            .onStart { emit(Resource.Loading) }
                            .onEach {
                                emit(Resource.Success(it))
                            }.catch { e ->
                                print("Error $e")
                                emit(Resource.Error(e as Exception))
                            }.collect {
                            }
                }
                is Action.Refresh -> {
                    _feedExecutor.execute()
                            .onStart { emit(Resource.Loading) }
                            .onEach {
                                emit(Resource.Success(it))
                            }.catch { e ->
                                emit(Resource.Error(e as Exception))
                            }.collect {
                            }
                }
            }
        }
    }

    fun setState(action: Action) = run { _action.value = action }
    sealed class Action {
        object Load : Action()
        object Refresh : Action()
    }


}