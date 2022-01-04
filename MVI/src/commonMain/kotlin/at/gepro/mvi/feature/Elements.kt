package at.gepro.mvi.feature

import kotlinx.coroutines.flow.Flow


typealias Bootstrapper<Action> = suspend () -> Flow<Action>

typealias Actor<State, Action, Effect> = suspend (state: State, action: Action) -> Flow<Effect>

typealias Reducer<State, Effect> = (state: State, effect: Effect) -> State

typealias PostProcessor<Action, Effect, State> =
            suspend (action: Action, effect: Effect, state: State) -> Flow<Action>?

typealias NewsPublisher<Action, Effect, State, News> =
            suspend (action: Action, effect: Effect, state: State) -> News?

typealias WishToAction<Wish, Action> = (wish: Wish) -> Action