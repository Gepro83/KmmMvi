package at.gepro.mvi.feature

import kotlinx.coroutines.flow.SharedFlow

interface Feature<Wish : Any, State : Any, News : Any> : Store<State> {
    val news: SharedFlow<News>

    fun accept(wish: Wish)

    fun start()

    fun dispose()
}