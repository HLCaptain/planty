package nest.planty.ui.login

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import nest.planty.Res
import nest.planty.ui.components.LoadingIndicator
import nest.planty.ui.components.PlantyDialogContent
import nest.planty.ui.dialog.LocalDialogDismissRequest

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        LoginDialogScreen(
            screenModel = getScreenModel<LoginScreenModel>()
        )
    }
}

@Composable
fun LoginDialogScreen(
    screenModel: LoginScreenModel,
) {
    val isUserSignedIn by screenModel.isUserSignedIn.collectAsState()
    val isUserSigningIn by screenModel.isSigningIn.collectAsState()
    val dismissDialog = LocalDialogDismissRequest.current
    LaunchedEffect(isUserSignedIn) {
        if (isUserSignedIn) dismissDialog()
    }
    LoginDialogContent(
        modifier = Modifier.fillMaxWidth(),
        isUserSigningIn = isUserSigningIn,
        signInAnonymously = screenModel::signInAnonymously
    )
}

@Composable
fun LoginDialogContent(
    modifier: Modifier = Modifier,
    isUserSigningIn: Boolean = false,
    signInAnonymously: () -> Unit = {},
) {
    Crossfade(
        modifier = modifier,
        targetState = isUserSigningIn,
        label = "Login Dialog Content"
    ) {
        if (it) {
            PlantyDialogContent(
                text = { LoadingIndicator() },
                textPaddingValues = PaddingValues()
            )
        } else {
            PlantyDialogContent(
                title = { LoginTitle() },
                text = {
                    LoginScreen(
                        modifier = Modifier.fillMaxWidth(),
                        signInAnonymously = signInAnonymously
                    )
                },
                buttons = { LoginButtons(modifier = Modifier.fillMaxWidth()) },
                containerColor = Color.Transparent,
            )
        }
    }
}

@Composable
fun LoginTitle() {
    Text(text = Res.string.login)
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    signInAnonymously: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = signInAnonymously
        ) {
            Text(
                text = Res.string.sign_in_anonymously,
                textAlign = TextAlign.Center
            )
        }
        // TODO: make login via email/password combo
        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            enabled = false,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = Res.string.email)
            }
        )
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            enabled = false,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = Res.string.password)
            }
        )
    }
}

@Composable
fun LoginButtons(
    modifier: Modifier = Modifier
) {
    val onDialogClosed = LocalDialogDismissRequest.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onDialogClosed() }
        ) {
            Text(text = Res.string.cancel)
        }
        Button(
            onClick = {
                // TODO: Login via email/password
            },
            enabled = false,
        ) {
            Text(text = Res.string.login)
        }
    }
}
