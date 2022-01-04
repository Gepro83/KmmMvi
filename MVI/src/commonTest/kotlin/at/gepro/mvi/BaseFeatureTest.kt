package at.gepro.mvi

import at.gepro.mvi.feature.BaseFeature
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertTrue

class BaseFeatureTest {

    sealed class Action {
        object Clear : Action()
        object Fill : Action()
    }

    data class State(val full : Boolean)

    data class News(val message: String)

    @Test
    fun `bootstrapper`() {
        val feature = BaseFeature<Action, Action, Action, State, News>(
            initialState = State(false),
            bootstrapper = { flowOf(Action.Fill) },
            actor = { state, action -> flowOf(action) },
            reducer = { state, effect ->
                when (effect) {
                    Action.Clear -> State(false)
                    Action.Fill -> State(true)
                }
            },
            wishToAction = { it }
        )

        assertTrue(feature.state.value.full)

    }
}