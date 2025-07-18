package com.zonedev.minapp.ui.Screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.Components.ButtonApp
import com.zonedev.minapp.ui.Components.CustomTextField
import com.zonedev.minapp.ui.Components.Modal
import com.zonedev.minapp.ui.Components.Space
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.bodyFontFamily
import com.zonedev.minapp.util.TestTags

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
@SuppressLint("UnusedBoxWithConstraintsScope")
@Preview
@Composable
fun BlobUi() {
    val blob = painterResource(R.drawable.blob)

    // 1. Usamos BoxWithConstraints para obtener las dimensiones de la pantalla.
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // 'maxWidth' y 'maxHeight' ahora están disponibles para usarlos en los modificadores.

        val dynamicFontSize = when {
            // Para pantallas de baja altura (ej. teléfonos en horizontal)
            maxHeight < 700.dp -> 30.sp
            // Para pantallas de altura media (ej. teléfonos en vertical)
            maxHeight < 1000.dp -> 35.sp
            // Para pantallas muy altas (ej. tablets)
            else -> 54.sp
        }
        // 2. La imagen del blob se alinea en la esquina superior derecha.
        //    Su tamaño y desplazamiento ahora son proporcionales al ancho de la pantalla,
        //    lo que garantiza que se escale correctamente en cualquier dispositivo.
        Image(
            painter = blob,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(maxWidth * 1.5f) // Por ejemplo, el 90% del ancho de la pantalla
                .offset(x = maxWidth * -0.15f, y = -(maxWidth * 0.5f)) // El desplazamiento también es proporcional
        )

        // 3. El texto se alinea arriba a la izquierda y se posiciona con padding.
        //    Los valores de padding son más seguros que los offsets para el espaciado interno.
        Text(
            text = stringResource(R.string.blob_ui_text), // "BIENVENIDO"
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = dynamicFontSize,
            fontFamily = bodyFontFamily,
            textAlign = TextAlign.Start, // Es mejor usar Start que Justify para textos cortos.
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 80.dp)
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
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            text_Tag= TestTags.EMAIL_FIELD,
        )

        Space(16.dp)

        CustomTextField(
            value = password,
            label = stringResource(R.string.Label_name_Input_password),
            onValueChange = { password = it },
            isPasswordField = true,
            iconTint =R.color.color_component,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            text_Tag=TestTags.PASSWORD_FIELD
        )

        Space(16.dp)

        ButtonApp(stringResource(R.string.name_button_login),
            onClick = {
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
            },
            modifier = Modifier.testTag(TestTags.LOGIN_BUTTON)
        )

        // Muestra el modal si showDialog es verdadero
        Modal(showDialog, { showDialog = false }, R.string.Title_Error, errorMessage, onClick = { showDialog = false })
    }
}