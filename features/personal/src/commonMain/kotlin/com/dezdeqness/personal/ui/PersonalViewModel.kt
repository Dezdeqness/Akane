package com.dezdeqness.personal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.personal.domain.repository.PersonalRepository
import com.dezdeqness.personal.ui.mapper.PersonalUiMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonalViewModel(
    private val personalRepository: PersonalRepository,
    private val personalUiMapper: PersonalUiMapper,
) : ViewModel() {

    val personalStateFlow: StateFlow<PersonalState> =
        personalRepository
            .getPersonalAsFlow()
            .map {
                PersonalState(it.map(personalUiMapper::toUiModel))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = PersonalState()
            )

    fun onItemUnFavouriteClicked(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            personalRepository.deleteById(id)
        }
    }
}
