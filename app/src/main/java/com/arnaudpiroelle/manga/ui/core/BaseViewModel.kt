package com.arnaudpiroelle.manga.ui.core

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<A : Action, S : State>(private val initialState: S) : ViewModel() {
    val state = MutableLiveData<S>()

    abstract fun handle(action: A)

    @MainThread
    protected fun updateState(reducer: (S) -> S) {
        val currentState = state.value ?: initialState

        state.value = reducer(currentState)
    }
}

interface Action
interface State