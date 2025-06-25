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
    var message by remember { mutableStateOf("") }

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
    // Usamos ButtonApp aquí también
    ButtonApp(stringResource(R.string.button_submit)) {
        if (subject.isEmpty() || observation.isEmpty()) {
            showDialog = true
            message = "Error"
        }else {
            val datos = mapOf(
                "Subject" to subject.lowercase(),
                "Observation" to observation.lowercase(),
                "Evidencias" to  evidencias.toString()
            )

            val parametros = crearParametrosParaReporte(tipo_report, datos)

            reporteViewModel.crearReporte(tipo_report,parametros,guardiaId)
            showDialog = true
            message = "Correcto"
        }
    }
    Separetor()

    // Mostrar el modal si showModal es true

    if (showDialog) {
        // Variables para el contenido dinámico del diálogo
        val dialogTitle: String
        val dialogContent: String
        val confirmButtonText: String

        // Determina el contenido del diálogo basado en el valor de 'message'
        when (message) {
            "Error" -> {
                dialogTitle = "Error" // Define en strings.xml
                dialogContent = "El reporte realizdo tiene campos en blanco" // Define en strings.xml
                confirmButtonText = stringResource(R.string.Value_Button_Report) // Reutiliza o define uno nuevo
            }
            "Correcto" -> {
                dialogTitle = stringResource(R.string.Name_Modal_Report)
                dialogContent = stringResource(R.string.Content_Modal_Report)// Define en strings.xml
                confirmButtonText = stringResource(R.string.Value_Button_Report)
            }
            // Puedes añadir más casos si tienes otros tipos de mensajes o errores
            else -> {
                // Caso por defecto o para otros mensajes que no estén mapeados
                dialogTitle = stringResource(R.string.Name_Modal_Report)
                dialogContent = stringResource(R.string.Content_Modal_Report)
                confirmButtonText = stringResource(R.string.Value_Button_Report)
            }
        }

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                subject = ""
                observation = ""
                evidencias = null
                message = "" // Limpia el mensaje al cerrar el diálogo
            },
            title = {
                Text(
                    text = dialogTitle, // Usa el título dinámico
                    color = primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            },
            text = {
                Text(
                    text = dialogContent, // Usa el contenido dinámico
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            },
            confirmButton = {
                ButtonApp(
                    text = confirmButtonText, // Usa el texto del botón dinámico
                    onClick = {
                        showDialog = false
                        subject = ""
                        observation = ""
                        evidencias = null
                        message = "" // Limpia el mensaje al cerrar el diálogo
                    },
                )
            }
        )
    }
}