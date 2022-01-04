package at.gepro.mvi.viewmodel

typealias UiEventToWish<UiEvent, Wish> = (UiEvent) -> Wish?

typealias StateToModel<State, Model> = (State) -> Model

typealias NewsToModel<News, Model> = (News) -> Model?

typealias NewsToLiveEvent<News, LiveEvent> = (News) -> LiveEvent?
