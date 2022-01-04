package at.gepro.mvi.viewmodel

interface MviView<UiEvent: Any, Model : Any> {

    fun render(viewModel: MviViewModel<UiEvent, Model>)

}