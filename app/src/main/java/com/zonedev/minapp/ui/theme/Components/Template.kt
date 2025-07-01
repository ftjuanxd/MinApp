package com.zonedev.minapp.ui.theme.Components


import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.theme.primary

@Composable
fun Template_Text(
    IsScreenElement: Boolean = false,
    Label_Id: String = stringResource(R.string.Value_Default_Label_Id),
    Tipo_Report: String = "Elemento",
    guardiaId: String
) {
    // Variables de los textfields
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var evidenciasUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    if (IsScreenElement) {
        Camera(
            imageUri = evidenciasUri,
            onImageCaptured = { uri -> evidenciasUri = uri },
            label = "Elemento"
        )
    }

    CustomTextField(
        value = id,
        label = Label_Id,
        onValueChange = { id = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    Space(8.dp)

    CustomTextField(
        value = name,
        label = "Nombre",
        onValueChange = { name = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )

    Space(8.dp)

    // La lambda ahora recibe el estado del checkbox "Hold" para decidir si resetea id y name.
    val onResetAction: (Boolean) -> Unit = { shouldHold ->
        // La evidencia siempre se resetea.
        evidenciasUri = Uri.EMPTY

        if (IsScreenElement) {
            // Si es la pantalla de elementos, solo reseteamos id y name si "Hold" está DESACTIVADO.
            if (!shouldHold) {
                id = ""
                name = ""
            }
        } else {
            // Para otras pantallas, siempre reseteamos id y name.
            id = ""
            name = ""
        }
    }

    Components_Template(id, name, Tipo_Report, evidenciasUri, guardiaId, onResetFields = onResetAction)
}

@Composable
fun Components_Template(
    id: String,
    name: String,
    tipo_report: String = "Elemento",
    evidenciasUri: Uri?,
    guardiaId: String,
    reporteViewModel: ReporteViewModel = viewModel(),
    onResetFields: (shouldHold: Boolean) -> Unit, // Lambda actualizada para pasar el estado de "Hold"
) {
    var destiny by remember { mutableStateOf("") }
    var auto by remember { mutableStateOf("") }
    var descrip by remember { mutableStateOf("") }

    // Estados para controlar la visibilidad de los diálogos
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showValidationErrorDialog by remember { mutableStateOf(false) }

    val holdCheckState = CheckHold()

    FieldsThemes(destiny, { destiny = it }, auto, { auto = it }, descrip, { descrip = it })

    // El botón principal ahora contiene la lógica de validación.
    ButtonApp(stringResource(R.string.button_submit)) {
        // --- LÓGICA DE VERIFICACIÓN ACTUALIZADA ---

        // 1. Verificamos si la evidencia es válida. Es obligatoria solo para "Elemento".
        val isEvidenceValid = if (tipo_report == "Elemento") {
            evidenciasUri != null && evidenciasUri != Uri.EMPTY
        } else {
            true // La evidencia no es necesaria para otros tipos de reporte.
        }

        // 2. Verificamos el resto de los campos y la evidencia.
        val isFormValid = id.isNotBlank() && name.isNotBlank() &&
                destiny.isNotBlank() && auto.isNotBlank() && descrip.isNotBlank() &&
                isEvidenceValid // Se añade la nueva condición.

        if (isFormValid) {
            // Si el formulario es válido, muestra el diálogo de confirmación.
            showConfirmationDialog = true
        } else {
            // Si es inválido, muestra un diálogo de error.
            showValidationErrorDialog = true
        }
    }

    Separator()

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.Name_Modal_Report),
                    color = colorResource(id = R.color.primary),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                )
                Space(8.dp)
            },
            text = {
                Text(
                    text = stringResource(R.string.Content_Modal_Report),
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            },
            confirmButton = {
                ButtonApp(
                    onClick = {
                        val datos = if (tipo_report != "Elemento") {
                            mapOf(
                                "Id_placa" to id.lowercase(),
                                "Name" to name.lowercase(),
                                "Destino" to destiny.lowercase(),
                                "Autorizacion" to auto.lowercase(),
                                "Descripcion" to descrip.lowercase()
                            )
                        } else {
                            mapOf(
                                "Imgelement" to evidenciasUri.toString(),
                                "Id_placa" to id.lowercase(),
                                "Name" to name.lowercase(),
                                "Destino" to destiny.lowercase(),
                                "Autorizacion" to auto.lowercase(),
                                "Descripcion" to descrip.lowercase()
                            )
                        }

                        val parametros = crearParametrosParaReporte(tipo_report, datos)
                        reporteViewModel.crearReporte(tipo_report, parametros, guardiaId)

                        // --- LÓGICA DE RESETEO ACTUALIZADA ---

                        // 1. Reseteamos los campos locales (destino, auto, descrip)
                        //    solo si el CheckBox "Hold" no está activo.
                        if (!holdCheckState.value) {
                            destiny = ""
                            auto = ""
                            descrip = ""
                        }

                        // 2. Invocamos la función del padre y le pasamos el estado de "Hold".
                        //    El padre (`Template_Text`) se encargará de resetear (o no)
                        //    los campos `id` y `name` según corresponda.
                        onResetFields(holdCheckState.value)

                        showConfirmationDialog = false
                    }, text = stringResource(id = R.string.Value_Button_Report)
                )
            }
        )
    }

    // --- DIÁLOGO DE ERROR DE VALIDACIÓN ---
    if (showValidationErrorDialog) {
        AlertDialog(
            onDismissRequest = { showValidationErrorDialog = false },
            title = {
                Text(
                    text = "Campos Incompletos",
                    color = primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                )
                Space(8.dp)
            },

            text = {
                Text(
                    text = "Por favor, asegúrese de llenar todos los campos antes de generar el reporte. Para reportes de elementos, la evidencia es obligatoria.",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp))
            },
            confirmButton = {
                ButtonApp(
                    text = stringResource(id = R.string.Value_Button_Report),
                    onClick = { showValidationErrorDialog = false }
                )
            }
        )
    }
}