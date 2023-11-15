package nest.planty.ui.dialog

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition
import nest.planty.ui.components.PlantyDialogContent


val LocalDialogDismissRequest = compositionLocalOf { {} }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantyDialog(
    startScreen: Screen,
    isDialogOpen: Boolean = true,
    onDialogClosed: () -> Unit = {},
) {
    if (isDialogOpen) {
        // Don't use exit animations because
        // it looks choppy while Dialog resizes due to content change.v
        val navigator = LocalNavigator.currentOrThrow
        val currentScreen = navigator.lastItem
        val firstScreen = navigator.items.first()
        val onDismissRequest: () -> Unit = {
            if (currentScreen == firstScreen) {
                onDialogClosed()
            } else {
                navigator.pop()
            }
        }
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = onDismissRequest,
        ) {
            PlantyDialogContent(
                surface = { PlantyDialogContent(content = it) },
            ) {
                CompositionLocalProvider(
                    LocalDialogDismissRequest provides onDismissRequest,
                ) {
                    Navigator(startScreen) {
                        ScreenTransition(
                            navigator = LocalNavigator.currentOrThrow,
                            transition = {
                                (slideInHorizontally(tween(200)) { it / 8 } + fadeIn(tween(200))) togetherWith
                                        (slideOutHorizontally(tween(200)) { -it / 8 } + fadeOut(tween(200)))
                            }
                        ) {
                            it.Content()
                        }
                    }
                }
            }
        }
    }
}