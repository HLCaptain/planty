package nest.planty.ui.login

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
            signInAnonymously = screenModel::signInAnonymously,
            signInWithEmailAndPassword = screenModel::signInWithEmailAndPassword,
            signUpWithEmailAndPassword = screenModel::signUpWithEmailAndPassword,
        )
    }
}

@Composable
fun LoginDialogContent(
    modifier: Modifier = Modifier,
    isUserSigningIn: Boolean = false,
    signInAnonymously: () -> Unit = {},
    signInWithEmailAndPassword: (email: String, password: String) -> Unit = { _, _ -> },
    signUpWithEmailAndPassword: (email: String, password: String) -> Unit = { _, _ -> },
) {
    Crossfade(
        modifier = modifier,
        targetState = isUserSigningIn,
        label = "Login Dialog Content"
    ) { userSignedIn ->
        if (userSignedIn) {
            PlantyDialogContent(
                text = { LoadingIndicator() },
                textPaddingValues = PaddingValues()
            )
        } else {
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            PlantyDialogContent(
                title = { LoginTitle() },
                text = {
                    LoginScreen(
                        modifier = Modifier.fillMaxWidth(),
                        signInAnonymously = signInAnonymously,
                        emailChanged = { email = it },
                        passwordChanged = { password = it },
                    )
                },
                buttons = {
                    LoginButtons(
                        modifier = Modifier.fillMaxWidth(),
                        signInWithEmailAndPassword = { signInWithEmailAndPassword(email, password) },
                        signUpWithEmailAndPassword = { signUpWithEmailAndPassword(email, password) },
                    )
                },
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
    emailChanged: (String) -> Unit = {},
    passwordChanged: (String) -> Unit = {},
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
        var email by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            enabled = true,
            onValueChange = {
                email = it
                emailChanged(it)
            },
            label = {
                Text(text = Res.string.email)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        var password by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            enabled = true,
            onValueChange = {
                password = it
                passwordChanged(it)
            },
            label = {
                Text(text = Res.string.password)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
fun LoginButtons(
    modifier: Modifier = Modifier,
    signInWithEmailAndPassword: () -> Unit = {},
    signUpWithEmailAndPassword: () -> Unit = {},
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
            onClick = signInWithEmailAndPassword,
            enabled = true,
        ) {
            Text(text = Res.string.login)
        }
        Button(
            onClick = signUpWithEmailAndPassword,
            enabled = true,
        ) {
            Text(text = Res.string.sign_up)
        }
    }
}
