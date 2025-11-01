package com.dezdeqness.designsystem.nestedscroll

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
fun ExpandedHeader(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    navigationBar: (@Composable () -> Unit)? = null,
) {
    val connection = LocalNestedScrollConnection.current

    SubcomposeLayout(modifier) { constraints ->
        val headerPlaceable = subcompose("header") {
            header()
        }.first().measure(constraints)

        val navBarPlaceable = subcompose("navBar") {
            navigationBar?.invoke()
        }.firstOrNull()?.measure(constraints)

        connection.maxHeight = headerPlaceable.height.toFloat()
        connection.minHeight = navBarPlaceable?.height?.toFloat() ?: 0f

        val space = IntSize(
            constraints.maxWidth,
            headerPlaceable.height + connection.headerOffset.roundToInt()
        )

        layout(space.width, space.height) {
            headerPlaceable.place(0, connection.headerOffset.roundToInt())
            navBarPlaceable?.place(
                Alignment.TopCenter.align(
                    IntSize(navBarPlaceable.width, navBarPlaceable.height),
                    space,
                    layoutDirection
                )
            )
        }
    }
}
