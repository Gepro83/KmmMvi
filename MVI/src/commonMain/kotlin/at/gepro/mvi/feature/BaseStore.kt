package at.gepro.mvi.feature

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BaseStore<State>(initialState: State) : Store<State> {

    private val _state = MutableStateFlow(initialState)

    override val state: StateFlow<State> = _state

    protected fun setState(state: State) {
        _state.value = state
    }
}