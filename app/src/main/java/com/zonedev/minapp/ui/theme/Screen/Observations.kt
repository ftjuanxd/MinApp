package com.zonedev.minapp.ui.theme.Screen

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Separetor
import com.zonedev.minapp.ui.theme.Components.crearParametrosParaReporte
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.theme.primary

@Composable
fun Observations(guardiaId: String){
    Components_Observations(guardiaId)
}
@Composable
fun Components_Observations(guardiaId: String,reporteViewModel: ReporteViewModel = viewModel()){
    var subject by remember { mutableStateOf("") }
    var observation by remember { mutableStateOf("") }
    var tipo_report ="Observations"
    var showDialog by remember { mutableStateOf(false) }

    var evidencias by remember { mutableStateOf<Uri?>(null) }
    //TextField Subject
    CustomTextField(
        value = subject,
        label = stringResource(R.string.label_subject),
        onValueChange = { subject = it },
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    CustomTextField(
        value = observation,
        label = stringResource(R.string.label_observations),
        onValueChange = { observation = it },
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        pdHeight = 200.dp
    )
    /*CaptureImageScreen("evidencias") { base64Image ->
        // Guarda el string base64 en tu colección de Firestore
        evidencias = base64Image
    }*/

    Separetor()
    // Usamos ButtonApp aquí también
    ButtonApp(stringResource(R.string.button_submit)) {
        val datos = mapOf(
            "Subject" to subject.lowercase(),
            "Observation" to observation.lowercase(),
            "Evidencias" to  evidencias.toString()
        )

        val parametros = crearParametrosParaReporte(tipo_report, datos)

        reporteViewModel.crearReporte(tipo_report,parametros,guardiaId)
        showDialog = true
    }

    // Mostrar el modal si showModal es true

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                subject = ""
                observation = ""
                evidencias = null
            },
            title = { Text(
                text = stringResource(R.string.Name_Modal_Report),
                color = primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) },
            text = { Text(
                text = stringResource(R.string.Content_Modal_Report),
                color = Color.Gray,
                modifier = Modifier
                    .padding(bottom = 6.dp)
            ) },
            confirmButton = {
                // Usa el botón personalizado dentro del modal
                ButtonApp(
                    text = stringResource(R.string.Value_Button_Report),
                    onClick = {
                        showDialog = false // Cierra el modal cuando se hace clic en "Aceptar"
                        subject = ""
                        observation = ""
                        evidencias = null
                    },
                    //modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }

}