package at.gepro.mvi.feature

import kotlinx.coroutines.flow.StateFlow

interface Store<State> {
    val state: StateFlow<State>
}