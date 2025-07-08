package com.zonedev.minapp.ui.theme.Screen.Guardia

import android.net.Uri
import android.widget.Toast
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
import com.zonedev.minapp.ui.theme.Components.Camera
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Modal
import com.zonedev.minapp.ui.theme.Components.Report.crearParametrosParaReporte
import com.zonedev.minapp.ui.theme.Components.Separator
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

    // --- Usamos Uri? e inicializamos a null ---
    var evidenciasUri by remember { mutableStateOf<Uri?>(null) }

    // --- Estados para el modal y la carga ---
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Camera(
        imageUri = evidenciasUri,
        onImageCaptured = { uri -> evidenciasUri = uri }
    )

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
        // --- El botón muestra estado de carga y se deshabilita ---
        text = if (isLoading) stringResource(R.string.enviando) else stringResource(R.string.button_submit),
        isEnabled = !isLoading,
        onClick = {
            if (subject.isBlank() || observation.isBlank()) {
                dialogMessage = "Error"
                showDialog = true
            } else {
                // --- Inicia la carga ---
                isLoading = true

                // Preparamos los datos base del reporte
                val datosBase = mapOf(
                    "Evidencias" to evidenciasUri.toString(),
                    "Titulo" to subject.lowercase(),
                    "Observacion" to observation.lowercase()
                    // Nota: Ya no ponemos "Evidencias" aquí
                )
                val parametros = crearParametrosParaReporte(tipo_report, datosBase)

                // Definimos los handlers para el resultado
                val onSuccessHandler = {
                    isLoading = false
                    dialogMessage = "Correcto"
                    showDialog = true
                    // Limpiamos los campos
                    subject = ""
                    observation = ""
                    evidenciasUri = null
                }

                val onFailureHandler: (Exception) -> Unit = { exception ->
                    isLoading = false
                    Toast.makeText(context, "Error al enviar: ${exception.message}", Toast.LENGTH_LONG).show()
                }

                // --- Decidimos qué función del ViewModel llamar ---
                if (evidenciasUri != null && evidenciasUri != Uri.EMPTY) {
                    // Si hay imagen, llamamos a la función que sube el archivo
                    reporteViewModel.subirImagenYCrearReporte(
                        uriLocal = evidenciasUri!!, // Usamos !! porque ya comprobamos que no es nulo
                        tipo = tipo_report,
                        parametros = parametros,
                        guardiaId = guardiaId,
                        onSuccess = onSuccessHandler,
                        onFailure = onFailureHandler
                    )
                } else {
                    // Si no hay imagen, agregamos el campo de evidencias vacío y llamamos a la función original
                    val parametrosSinEvidencia = parametros.toMutableMap()
                    parametrosSinEvidencia["Evidencias"] = "Ninguna"

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
            else -> { // Manejo por defecto para evitar errores
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