package at.gepro.mvi.feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseFeature<Wish : Any, in Action : Any, in Effect : Any, State : Any, News : Any>(
    initialState: State,
    coroutineContext: CoroutineContext = Dispatchers.Main,
    private val bootstrapper: Bootstrapper<Action>? = null,
    private val wishToAction: WishToAction<Wish, Action>,
    private val actor: Actor<State, Action, Effect>,
    private val reducer: Reducer<State, Effect>,
    private val postProcessor: PostProcessor<Action, Effect, State>? = null,
    private val newsPublisher: NewsPublisher<Action, Effect, State, News>? = null
) : Feature<Wish, State, News>, BaseStore<State>(initialState) {

    private val mutableNews : MutableSharedFlow<News> = MutableSharedFlow()

    override val news: SharedFlow<News> = mutableNews

    private val scope = CoroutineScope(coroutineContext)

    override fun start() {
        scope.launch {
            bootstrapper?.invoke()?.collect { action ->
                callActor(action)
            }
        }
    }

    override fun accept(wish: Wish) {
        callActor(wishToAction(wish))
    }

    private fun callActor(action: Action) {
        val oldState = state.value
        scope.launch {
            actor.invoke(oldState, action).collect { effect ->
                callReducer(effect)
                callNewsPublisher(action, effect)
                callPostProcessor(action, effect)
            }
        }
    }

    private fun callReducer(effect : Effect) {
        setState(reducer.invoke(state.value, effect))
    }

    private fun callNewsPublisher(action: Action, effect: Effect) {
        scope.launch {
            newsPublisher?.invoke(action, effect, state.value)?.let { news ->
                mutableNews.emit(news)
            }
        }
    }

    private fun callPostProcessor(action: Action, effect: Effect) {
        scope.launch {
            postProcessor?.invoke(action, effect, state.value)?.collect {
                callActor(it)
            }
        }
    }

    override fun dispose() {
        scope.cancel()
    }

}