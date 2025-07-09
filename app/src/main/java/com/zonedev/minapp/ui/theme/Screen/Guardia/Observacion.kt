package com.zonedev.minapp.ui.theme.Screen.Guardia

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.ImagePicker
import com.zonedev.minapp.ui.theme.Components.Modal
import com.zonedev.minapp.ui.theme.Components.Report.crearParametrosParaReporte
import com.zonedev.minapp.ui.theme.Components.Separator
import com.zonedev.minapp.ui.theme.Components.Space
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel

@Composable
fun Observations(guardiaId: String) {
    Components_Observations(guardiaId)
}

@Composable
fun Components_Observations(guardiaId: String, reporteViewModel: ReporteViewModel = viewModel()) {
    var subject by remember { mutableStateOf("") }
    var observation by remember { mutableStateOf("") }
    val tipo_report = stringResource(R.string.Name_Minuta_Obs)
    var evidenciasUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column {
        ImagePicker(
            selectedUris = evidenciasUris,
            onImagesSelected = { uris -> evidenciasUris = uris },
            allowMultiple = true // MODO MÚLTIPLE
        )

        Space()

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

        ButtonApp(
            text = if (isLoading) stringResource(R.string.enviando) else stringResource(R.string.button_submit),
            isEnabled = !isLoading,
            onClick = {
                if (subject.isBlank() || observation.isBlank()) {
                    dialogMessage = "Error"
                    showDialog = true
                } else {
                    isLoading = true
                    val datosBase = mapOf(
                        "Titulo" to subject.lowercase(),
                        "Observacion" to observation.lowercase()
                    )
                    val parametros = crearParametrosParaReporte(tipo_report, datosBase)
                    val onSuccessHandler = {
                        isLoading = false
                        dialogMessage = "Correcto"
                        showDialog = true
                        subject = ""
                        observation = ""
                        evidenciasUris = emptyList()
                    }
                    val onFailureHandler: (Exception) -> Unit = { exception ->
                        isLoading = false
                        Toast.makeText(context, "Error al enviar: ${exception.message}", Toast.LENGTH_LONG).show()
                    }

                    if (evidenciasUris.isNotEmpty()) {
                        reporteViewModel.subirImagenesYCrearReporte(
                            urisLocales = evidenciasUris,
                            tipo = tipo_report,
                            parametros = parametros,
                            guardiaId = guardiaId,
                            onSuccess = onSuccessHandler,
                            onFailure = onFailureHandler
                        )
                    } else {
                        val parametrosSinEvidencia = parametros.toMutableMap()
                        parametrosSinEvidencia["Evidencias"] = emptyList<String>()
                        reporteViewModel.crearReporte(
                            tipo = tipo_report,
                            parametros = parametrosSinEvidencia,
                            guardiaId = guardiaId,
                            onSuccess = onSuccessHandler,
                            onFailure = onFailureHandler
                        )
                    }
                }
            }
        )
        Separator()

        if (showDialog) {
            val dialogTitle: Int
            val dialogContent: Int
            when (dialogMessage) {
                "Error" -> {
                    dialogTitle = R.string.Title_Error
                    dialogContent = R.string.Mensaje_Por_Campos_Vacios
                }
                "Correcto" -> {
                    dialogTitle = R.string.Name_Modal_Report
                    dialogContent = R.string.Content_Modal_Report
                }
                else -> {
                    dialogTitle = 0
                    dialogContent = 0
                }
            }
            if (dialogTitle != 0) {
                Modal(
                    showDialog = showDialog,
                    onDismiss = {
                        showDialog = false
                        dialogMessage = ""
                    },
                    title = dialogTitle,
                    Message = dialogContent,
                    onClick = { showDialog = false }
                )
            }
        }
    }
}