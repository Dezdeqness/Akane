package com.dezdeqness.feed.ui.composable

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.designsystem.icons.AkaneIcons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedSearch(
    modifier: Modifier = Modifier,
    onQueryChanged: (String) -> Unit,
) {
    val searchBarState = rememberSearchBarState()

    val textFieldState = rememberTextFieldState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(searchBarState.currentValue, textFieldState.text) {
        if (searchBarState.currentValue == SearchBarValue.Collapsed && textFieldState.text.isEmpty()) {
            onQueryChanged("")
        }
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                textStyle = AppTheme.typography.titleMedium.copy(
                    color = AppTheme.colors.textPrimary,
                ),
                onSearch = {
                    onQueryChanged(textFieldState.text.toString())
                    scope.launch { searchBarState.animateToCollapsed() }
                },
                placeholder = {
                    Text(
                        text = "Восстание Лелуша",
                        style = AppTheme.typography.titleMedium.copy(
                            color = AppTheme.colors.textPrimary.copy(alpha = 0.8f),
                        ),
                    )
                },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {

                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(
                                AkaneIcons.Back,
                                contentDescription = null,
                                tint = AppTheme.colors.onSurface
                            )
                        }
                    } else {
                        Icon(
                            AkaneIcons.Search,
                            contentDescription = null,
                            tint = AppTheme.colors.onSurface
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                                    onQueryChanged("")
                                }
                                textFieldState.setTextAndPlaceCursorAtEnd("")
                            },
                        ) {
                            Icon(
                                AkaneIcons.Close,
                                contentDescription = null,
                                tint = AppTheme.colors.onSurface
                            )
                        }
                    }
                },
                colors = SearchBarDefaults.inputFieldColors(
                    unfocusedContainerColor = AppTheme.colors.onPrimary,
                    focusedContainerColor = AppTheme.colors.background,
                )
            )
        }

    TopSearchBar(
        state = searchBarState,
        inputField = inputField,
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
        colors = SearchBarDefaults.colors(
            containerColor = AppTheme.colors.background,
        )
    ) {}
}
