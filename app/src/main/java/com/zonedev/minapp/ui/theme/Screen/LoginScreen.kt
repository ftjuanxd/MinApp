package com.zonedev.minapp.ui.theme.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.MinappTheme
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.bodyFontFamily

@Composable
fun LoginApp(navController: NavController, auth: FirebaseAuth, onLoginSuccess: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BlobUi()
        Spacer(modifier = Modifier.height((-10).dp)) // Reduce la altura entre componentes
        CustomLoginScreen(navController, auth, onLoginSuccess)
    }
}

@Composable
fun BlobUi() {
    val blob = painterResource(R.drawable.blob)
    Box(modifier = Modifier.wrapContentHeight()) {
        Image(
            painter = blob,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopEnd,
            modifier = Modifier.absoluteOffset(x = (-60).dp, y = (-160).dp)
        )
        Text(
            text = stringResource(R.string.blob_ui_text),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = bodyFontFamily,
            textAlign = TextAlign.Start,
            modifier = Modifier.absoluteOffset(x = 20.dp, y = 80.dp)
        )
    }
}

@Composable
fun CustomLoginScreen(navController: NavController, auth: FirebaseAuth, onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") } // Variable para el mensaje de error
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            value = email,
            label = stringResource(R.string.Label_name_input_user),
            onValueChange = { if (it.length <= 254) email = it },
        )

        CustomTextField(
            value = password,
            label = stringResource(R.string.Label_name_Input_password),
            onValueChange = { password = it },
            isPasswordField = true
        )

        ButtonApp(stringResource(R.string.name_button_login)) {
            if (email.isBlank() || password.isBlank()) {
                // Muestra un mensaje de error si los campos están vacíos
                errorMessage = "Ingrese el correo electrónico como la contraseña."
                showDialog = true
            } else {
                // Realiza la autenticación si ambos campos tienen valores
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.let { user ->
                            onLoginSuccess(user.uid) // Pasa el userId de vuelta al MainActivity
                        }
                    } else {
                        errorMessage = "Los datos de usuario son incorrectos. Por favor, inténtalo de nuevo."
                        showDialog = true
                        email = ""
                        password = ""
                    }
                }
            }
        }

        // Muestra el modal si showDialog es verdadero
        Modal(showDialog = showDialog, onDismiss = { showDialog = false }, errorMessage = errorMessage)
    }
}

@Composable
fun Modal(showDialog: Boolean, onDismiss: () -> Unit, errorMessage: String) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Error") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                ButtonApp(
                    text = stringResource(R.string.Value_Button_Report),
                    onClick = onDismiss,
                )
            }
        )
    }
}

// Función Composable para el preview de Modal
@Composable
fun PreviewModalDialog() {
    MinappTheme { // Envuelve tu componente con tu tema para que los colores y la tipografía se apliquen
        Modal(
            showDialog = true, // Establece showDialog a true para que el diálogo sea visible en el preview
            onDismiss = { false },
            errorMessage = "¡Ha ocurrido un error inesperado! Por favor, inténtalo de nuevo."
        )
    }
}


@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewModalDialogDark() {
    MinappTheme(darkTheme = true) { // Fuerza el tema oscuro para este preview
        Modal(
            showDialog = true,
            onDismiss = { /* No se necesita una implementación real para el preview */ },
            errorMessage = "¡Ha ocurrido un error inesperado! Por favor, inténtalo de nuevo."
        )
    }
}
