package com.zonedev.minapp.ui.theme.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.google.firebase.auth.FirebaseAuth
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Modal
import com.zonedev.minapp.ui.theme.Components.Space
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.bodyFontFamily

@Composable
fun LoginApp(auth: FirebaseAuth, onLoginSuccess: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        BlobUi()
        CustomLoginScreen(auth, onLoginSuccess)
    }
}
@Preview
@Composable
fun BlobUi() {
    val blob = painterResource(R.drawable.blob)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = blob,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopEnd,
            modifier = Modifier.absoluteOffset(x = (-60).dp, y = (-160).dp)
        )
        Text(
            text = stringResource(R.string.blob_ui_text),
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 37.sp    ,
            fontFamily = bodyFontFamily,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .absoluteOffset(x = 10.dp, y = 80.dp)
        )
    }
}

@Composable
fun CustomLoginScreen(auth: FirebaseAuth, onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage: Int by remember { mutableStateOf(0) } // Variable para el mensaje de error
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
            .padding(top = 160.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            value = email,
            label = stringResource(R.string.Label_name_input_user),
            onValueChange = { if (it.length <= 254) email = it },
        )

        Space(16.dp)

        CustomTextField(
            value = password,
            label = stringResource(R.string.Label_name_Input_password),
            onValueChange = { password = it },
            isPasswordField = true,
            iconTint =R.color.color_component
        )

        Space(16.dp)

        ButtonApp(stringResource(R.string.name_button_login)) {
            if (email.isBlank() || password.isBlank()) {
                // Muestra un mensaje de error si los campos están vacíos
                errorMessage = R.string.Campos_Vacios_Login
                showDialog = true
            } else {
                // Realiza la autenticación si ambos campos tienen valores
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.let { user ->
                            onLoginSuccess(user.uid) // Pasa el userId de vuelta al MainActivity
                        }
                    } else {
                        errorMessage = R.string.Parametros_Incorrectos_Login
                        showDialog = true
                        email = ""
                        password = ""
                    }
                }
            }
        }

        // Muestra el modal si showDialog es verdadero
        Modal(showDialog, { showDialog = false }, R.string.Title_Error, errorMessage, onClick = { showDialog = false })
    }
}