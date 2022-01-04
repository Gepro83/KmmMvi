package at.gepro.mvi.viewmodel

import at.gepro.mvi.feature.BaseStore
import at.gepro.mvi.feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<UiEvent : Any, Model: Any, LiveEvent: Any, Wish: Any, State: Any, News: Any>(
    private val feature: Feature<Wish, State, News>,
    private val stateToModel: StateToModel<State, Model>,
    private val eventToWish: UiEventToWish<UiEvent, Wish>? =  null,
    private val newsToModel: NewsToModel<News, Model>? = null,
    private val newsToLiveEvent: NewsToLiveEvent<News, LiveEvent>? = null,
    coroutineContext: CoroutineContext = Dispatchers.Main
) : MviViewModel<UiEvent, Model, LiveEvent>, BaseStore<Model>(
    initialState = stateToModel.invoke(feature.state.value)
) {

    private val scope = CoroutineScope(coroutineContext)

    private val _singleLiveEvents = MutableSharedFlow<LiveEvent>()

    override val singleLiveEvents : SharedFlow<LiveEvent> = _singleLiveEvents

    init {
        scope.launch {
            feature.state.collect {
                setState(stateToModel.invoke(it))
            }
            if (newsToModel != null || newsToLiveEvent != null)
                feature.news.collect { news ->
                    newsToModel?.invoke(news)?.let {
                        setState(it)
                    }
                    newsToLiveEvent?.invoke(news)?.let {
                        publish(it)
                    }
                }
        }
    }

    protected fun publish(liveEvent: LiveEvent) {
        scope.launch {
            _singleLiveEvents.emit(liveEvent)
        }
    }

    override fun dispatch(event: UiEvent) {
        eventToWish?.invoke(event)?.let {
            feature.accept(it)
        }
    }

    override fun dispose() {
        scope.cancel()
    }
}
