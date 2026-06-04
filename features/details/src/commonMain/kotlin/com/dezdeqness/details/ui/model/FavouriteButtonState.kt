package com.dezdeqness.details.ui.model

sealed interface FavouriteButtonState {
    data class Loaded(val isFavourite: Boolean) : FavouriteButtonState
    data object Loading : FavouriteButtonState
    data object Hidden : FavouriteButtonState
}
