package at.gepro.mvi.viewmodel

import at.gepro.mvi.feature.Store
import kotlinx.coroutines.flow.SharedFlow

interface MviViewModel<UiEvent: Any, Model: Any, LiveEvent: Any> : Store<Model> {

    val singleLiveEvents : SharedFlow<LiveEvent>

    fun dispatch(event: UiEvent)

    fun dispose()
}