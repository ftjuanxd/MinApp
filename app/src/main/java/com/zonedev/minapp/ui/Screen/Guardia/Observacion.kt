package com.zonedev.minapp.ui.Screen.Guardia

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zonedev.minapp.R
import com.zonedev.minapp.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.Components.ButtonApp
import com.zonedev.minapp.ui.Components.CustomTextField
import com.zonedev.minapp.ui.Components.ImagePicker
import com.zonedev.minapp.ui.Components.Modal
import com.zonedev.minapp.ui.Components.Report.crearParametrosParaReporte
import com.zonedev.minapp.ui.Components.Separator
import com.zonedev.minapp.ui.Components.Space
import com.zonedev.minapp.util.ObservationsTestTags

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
            allowMultiple = true
        )

        Space(4.dp)

        CustomTextField(
            value = subject,
            label = stringResource(R.string.label_subject),
            onValueChange = { subject = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            text_Tag=ObservationsTestTags.SUBJECT_FIELD
        )
        CustomTextField(
            value = observation,
            label = stringResource(R.string.label_observations),
            onValueChange = { observation = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            pdHeight = 180.dp,
            text_Tag= ObservationsTestTags.OBSERVATION_FIELD
        )

        ButtonApp(
            text = if (isLoading) stringResource(R.string.enviando) else stringResource(R.string.button_submit),
            isEnabled = !isLoading,
            modifier = Modifier.testTag(ObservationsTestTags.SUBMIT_BUTTON),
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
                        // Para pruebas, es mejor manejar el estado de la UI que mostrar un Toast
                        dialogMessage = "NetworkError" // Podríamos usar un nuevo estado
                        showDialog = true
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
            val modalTestTag: String

            when (dialogMessage) {
                "Error" -> {
                    dialogTitle = R.string.Title_Error
                    dialogContent = R.string.Mensaje_Por_Campos_Vacios
                    modalTestTag = ObservationsTestTags.ERROR_MODAL
                }
                "Correcto" -> {
                    dialogTitle = R.string.Name_Modal_Report
                    dialogContent = R.string.Content_Modal_Report
                    modalTestTag = ObservationsTestTags.SUCCESS_MODAL
                }
                "NetworkError"-> { // Incluye "NetworkError" y otros casos
                    dialogTitle = R.string.Title_Error // Título genérico de error
                    dialogContent = R.string.Error_Network // Un nuevo string genérico
                    modalTestTag = ObservationsTestTags.ERROR_MODAL
                }else -> {
                    dialogTitle = 0
                    dialogContent =0
                    modalTestTag = ""
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
                    onClick = { showDialog = false },
                    modifier = Modifier.testTag(modalTestTag)
                )
            }
        }
    }
}
