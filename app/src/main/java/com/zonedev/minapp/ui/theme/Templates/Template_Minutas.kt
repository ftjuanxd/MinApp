package com.zonedev.minapp.ui.theme.Templates

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
import com.zonedev.minapp.ui.theme.Components.CheckHold
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Modal
import com.zonedev.minapp.ui.theme.Components.Report.crearParametrosParaReporte
import com.zonedev.minapp.ui.theme.Components.Separator
import com.zonedev.minapp.ui.theme.Components.Space
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel

@Composable
fun Components_Template(
    id: String,
    name: String,
    tipo_report: String = stringResource(R.string.Name_Minuta_Ele),
    evidenciasUri: Uri?,
    guardiaId: String,
    reporteViewModel: ReporteViewModel = viewModel(),
    onResetFields: (shouldHold: Boolean) -> Unit,
) {
    var destiny by remember { mutableStateOf("") }
    var auto by remember { mutableStateOf("") }
    var descrip by remember { mutableStateOf("") }

    // Estado para el diálogo de confirmación de envío
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // --- CAMBIO: Estados unificados para todos los errores de validación ---
    var showValidationErrorDialog by remember { mutableStateOf(false) }
    var validationErrorTitle by remember { mutableStateOf(0) }
    var validationErrorMessage by remember { mutableStateOf(0) }

    val holdCheckState = CheckHold()

    FieldsThemes(destiny, { destiny = it }, auto, { auto = it }, descrip, { descrip = it })

    ButtonApp(stringResource(R.string.button_submit)) {
        // --- CAMBIO: Lógica de validación centralizada ---
        val isEvidenceValid = if (tipo_report == "Elemento") evidenciasUri != null && evidenciasUri != Uri.EMPTY else true

        when {
            // 1. Validar campos vacíos
            id.isBlank() || name.isBlank() || destiny.isBlank() || auto.isBlank() || descrip.isBlank() -> {
                validationErrorTitle = R.string.Campos_Incompletos
                validationErrorMessage = R.string.Mensaje_Por_Campos_Vacios
                showValidationErrorDialog = true
            }
            // 2. Validar que la evidencia (si es necesaria) no esté vacía
            !isEvidenceValid -> {
                validationErrorTitle = R.string.Error_Validacion_Titulo // Necesitas este String
                validationErrorMessage = R.string.Error_Falta_Evidencia // Necesitas este String
                showValidationErrorDialog = true
            }
            // 3. Validar tipo de dato para ID
            !id.all { it.isDigit() } -> {
                validationErrorTitle = R.string.Error_Validacion_Titulo
                validationErrorMessage = R.string.Error_ID_Debe_Ser_Numero // Necesitas este String
                showValidationErrorDialog = true
            }
            // 4. Validar tipo de dato para campos de texto
            !name.all { it.isLetter() || it.isWhitespace() } ||
                    !destiny.all { it.isLetter() || it.isWhitespace() } ||
                    !auto.all { it.isLetter() || it.isWhitespace() } -> {
                validationErrorTitle = R.string.Error_Validacion_Titulo
                validationErrorMessage = R.string.Error_Campos_Debe_Ser_Texto // Necesitas este String
                showValidationErrorDialog = true
            }
            // Si todo es correcto
            else -> {
                showConfirmationDialog = true
                val datos = if (tipo_report != "Elemento") {
                    mapOf(
                        "Id_placa" to id.lowercase(),
                        "Nombre" to name.lowercase(),
                        "Destino" to destiny.lowercase(),
                        "Autorizacion" to auto.lowercase(),
                        "Descripcion" to descrip.lowercase()
                    )
                } else {
                    mapOf(
                        "Imgelement" to evidenciasUri.toString(),
                        "Id_placa" to id.lowercase(),
                        "Nombre" to name.lowercase(),
                        "Destino" to destiny.lowercase(),
                        "Autorizacion" to auto.lowercase(),
                        "Descripcion" to descrip.lowercase()
                    )
                }
                val parametros = crearParametrosParaReporte(tipo_report, datos)
                reporteViewModel.crearReporte(tipo_report, parametros, guardiaId)

                if (!holdCheckState.value) {
                    destiny = ""
                    auto = ""
                    descrip = ""
                }
                onResetFields(holdCheckState.value)
            }
        }
    }

    Separator()

    // Diálogo de confirmación (cuando todo está bien)
    if (showConfirmationDialog) {
        Modal(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            title = R.string.Name_Modal_Report,
            Message = R.string.Content_Modal_Report,
            onClick = { showConfirmationDialog = false }
        )
    }

    // Modal unificado para todos los errores de validación ---
    if (showValidationErrorDialog) {
        Modal(
            showDialog = showValidationErrorDialog,
            onDismiss = { showValidationErrorDialog = false },
            title = validationErrorTitle,
            Message = validationErrorMessage,
            onClick = { showValidationErrorDialog = false }
        )
    }
}


@Composable
fun FieldsThemes(destiny:String,onDestinyChange: (String) -> Unit,auto:String,onAutoChange: (String) -> Unit,descrip:String,onDescripChange: (String) -> Unit){

    CustomTextField(
        value = destiny,
        label = stringResource(R.string.Label_Destino_Report),
        onValueChange = { newValue ->
            // Solo permite letras y espacios en el campo de destino
            if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                onDestinyChange(newValue)
            }
        },
        isEnabled = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    Space()
    //TextField Authorization
    CustomTextField(
        value = auto,
        label = stringResource(R.string.Label_Autorizacion_Report),
        onValueChange = { newValue ->
            // Solo permite letras y espacios en el campo de autorización
            if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                onAutoChange(newValue)
            }
        },
        isEnabled = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    Space()
    //TextField Description
    CustomTextField(
        value = descrip,
        label = stringResource(R.string.Label_Descripcion_Report),
        onValueChange = onDescripChange, // Para descripción, permitimos cualquier caracter
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        pdHeight = 80.dp
    )
}

@Composable
fun Template_Text(
    IsScreenElement: Boolean = false,
    Label_Id: String = stringResource(R.string.Value_Default_Label_Id),
    Tipo_Report: String = stringResource(R.string.Name_Minuta_Ele),
    guardiaId: String
) {
    // --- CAMBIO: El estado de 'id' ahora es Int? (nullable Int) ---
    var id by remember { mutableStateOf<Long?>(null) }
    var name by remember { mutableStateOf("") }
    var evidenciasUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    if (IsScreenElement) {
        Camera(
            imageUri = evidenciasUri,
            onImageCaptured = { uri -> evidenciasUri = uri },
            label = stringResource(R.string.Name_Minuta_Ele)
        )
    }

    CustomTextField(
        // --- CAMBIO: Mostramos el 'id' como String, o vacío si es nulo ---
        value = id?.toString() ?: "",
        label = Label_Id,
        onValueChange = { newString ->
            // Filtramos para asegurarnos que solo sean dígitos
            val filteredString = newString.filter { it.isDigit() }
            // --- CAMBIO: Convertimos el string a Int?, o nulo si está vacío ---
            id = if (filteredString.isNotEmpty()) filteredString.toLongOrNull() else null
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
        )
    )
    Space()

    CustomTextField(
        value = name,
        label = stringResource(R.string.Label_Nombre_Report),
        onValueChange = { newValue ->
            // Solo permite letras y espacios en el campo de nombre
            if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                name = newValue
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )

    Space()

    val onResetAction: (Boolean) -> Unit = { shouldHold ->
        evidenciasUri = Uri.EMPTY

        if (IsScreenElement) {
            if (!shouldHold) {
                // --- CAMBIO: Reseteamos 'id' a null ---
                id = null
                name = ""
            }
        } else {
            // --- CAMBIO: Reseteamos 'id' a null ---
            id = null
            name = ""
        }
    }

    // --- CAMBIO: Pasamos el 'id' como String a Components_Template ---
    Components_Template(id?.toString() ?: "", name, Tipo_Report, evidenciasUri, guardiaId, onResetFields = onResetAction)
}
