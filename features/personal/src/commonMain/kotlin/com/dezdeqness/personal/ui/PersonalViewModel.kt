package com.dezdeqness.personal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.personal.domain.repository.PersonalRepository
import com.dezdeqness.personal.ui.mapper.PersonalUiMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonalViewModel(
    private val personalRepository: PersonalRepository,
    private val personalUiMapper: PersonalUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val personalStateFlow: StateFlow<PersonalState> =
        personalRepository
            .getPersonalAsFlow()
            .map {
                PersonalState(it.map(personalUiMapper::toUiModel))
            }
            .flowOn(coroutineDispatcherProvider.io())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = PersonalState()
            )

    fun onItemUnFavouriteClicked(id: Long) {
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            personalRepository.deleteById(id)
        }
    }
}
