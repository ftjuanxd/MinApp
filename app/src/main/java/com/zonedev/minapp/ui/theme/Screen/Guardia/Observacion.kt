package com.zonedev.minapp.ui.theme.Screen.Guardia

import android.net.Uri
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.Camera
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Modal
import com.zonedev.minapp.ui.theme.Components.Report.crearParametrosParaReporte
import com.zonedev.minapp.ui.theme.Components.Separator
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel

@Composable
fun Observations(guardiaId: String){
    Components_Observations(guardiaId)
}
@Composable
fun Components_Observations(guardiaId: String,reporteViewModel: ReporteViewModel = viewModel()){
    var subject by remember { mutableStateOf("") }
    var observation by remember { mutableStateOf("") }
    var tipo_report =stringResource(R.string.Name_Minuta_Obs)
    var showDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var evidenciasUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    Camera(
        imageUri = evidenciasUri,
        onImageCaptured = { uri -> evidenciasUri = uri }
    )

    //TextField Subject
    CustomTextField(
        value = subject,
        label = stringResource(R.string.label_subject),
        onValueChange = { subject = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    CustomTextField(
        value = observation,
        label = stringResource(R.string.label_observations),
        onValueChange = { observation = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        pdHeight = 200.dp
    )

    // Usamos ButtonApp aquí también
    ButtonApp(stringResource(R.string.button_submit)) {
        if (subject.isEmpty() || observation.isEmpty()) {
            showDialog = true
            message = "Error"
        }else {
            val datos = mapOf(
                "Titulo" to subject.lowercase(),
                "Observacion" to observation.lowercase(),
                "Evidencias" to evidenciasUri.toString()
            )

            val parametros = crearParametrosParaReporte(tipo_report, datos)

            reporteViewModel.crearReporte(tipo_report,parametros,guardiaId)
            showDialog = true
            message = "Correcto"
        }
        subject = ""
        observation = ""
        evidenciasUri = Uri.EMPTY
    }
    Separator()

    // Mostrar el modal si showModal es true

    if (showDialog) {
        // Variables para el contenido dinámico del diálogo
        var dialogTitle: Int =0
        var dialogContent: Int=0

        when (message) {
            "Error" -> {
                dialogTitle = R.string.Title_Error
                dialogContent =R.string.Mensaje_Por_Campos_Vacios
            }
            "Correcto" -> {
                dialogTitle = R.string.Name_Modal_Report
                dialogContent = R.string.Content_Modal_Report
            }
        }
        Modal(
            showDialog,
            {
                showDialog = false
                message=""
            },
            dialogTitle, dialogContent, onClick = {showDialog = false}
        )

    }
}