package com.zonedev.minapp.ui.theme.Templates

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
import com.zonedev.minapp.ui.theme.Components.CheckHold
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.ImagePicker
import com.zonedev.minapp.ui.theme.Components.Modal
import com.zonedev.minapp.ui.theme.Components.PlacaVisualTransformation
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

    // Estados unificados para todos los errores de validación ---
    var showValidationErrorDialog by remember { mutableStateOf(false) }
    var validationErrorTitle by remember { mutableStateOf(0) }
    var validationErrorMessage by remember { mutableStateOf(0) }

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val holdCheckState = CheckHold()

    FieldsThemes(destiny, { destiny = it }, auto, { auto = it }, descrip, { descrip = it })

    ButtonApp(
        // ---  El texto del botón cambia y se deshabilita si está cargando ---
        text = if (isLoading) stringResource(R.string.enviando) else stringResource(R.string.button_submit),
        isEnabled = !isLoading,
        onClick = {
            val isIdInvalid = when (tipo_report) {
                "Vehicular" -> {
                    // Para vehículos, valida el formato LLL-NNN
                    val plateRegex = Regex("^[A-Z]{3}-\\d{3}$")
                    !plateRegex.matches(id.uppercase())
                }
                else -> {
                    // Para otros reportes, valida que sea un número
                    !id.all { it.isDigit() }
                }
            }

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
                isIdInvalid -> {
                    validationErrorTitle = R.string.Error_Validacion_Titulo
                    // El mensaje de error también es dinámico
                    validationErrorMessage = if (tipo_report == "Vehicular") {
                        R.string.Error_Formato_Placa_Invalido // Necesitarás este nuevo string
                    } else {
                        R.string.Error_ID_Debe_Ser_Numero
                    }
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
                else -> {
                    // --- CAMBIO: Inicia la carga ---
                    isLoading = true

                    // Preparamos los datos base que son comunes
                    val datosBase = mapOf(
                        "Id_placa" to id.lowercase(),
                        "Nombre" to name.lowercase(),
                        "Destino" to destiny.lowercase(),
                        "Autorizacion" to auto.lowercase(),
                        "Descripcion" to descrip.lowercase()
                    )
                    // Usamos la función externa para construir el mapa de parámetros
                    val parametros = crearParametrosParaReporte(tipo_report, datosBase)

                    // Definimos los handlers para el resultado
                    val onSuccessHandler = {
                        isLoading = false
                        showConfirmationDialog = true
                        if (!holdCheckState.value) {
                            destiny = ""
                            auto = ""
                            descrip = ""
                        }
                        onResetFields(holdCheckState.value)
                    }

                    val onFailureHandler: (Exception) -> Unit = { exception ->
                        isLoading = false
                        Toast.makeText(context, "Error al enviar: ${exception.message}", Toast.LENGTH_LONG).show()
                    }

                    // --- Decidimos qué función del ViewModel llamar ---
                    if (tipo_report == "Elemento" && evidenciasUri != null && evidenciasUri != Uri.EMPTY) {
                        // Si hay imagen, llamamos a la función que sube el archivo
                        reporteViewModel.subirImagenYCrearReporte(
                            uriLocal = evidenciasUri,
                            tipo = tipo_report,
                            parametros = parametros,
                            guardiaId = guardiaId,
                            onSuccess = onSuccessHandler,
                            onFailure = onFailureHandler
                        )
                    } else {
                        // Si no hay imagen, llamamos a la función original
                        reporteViewModel.crearReporte(
                            tipo = tipo_report,
                            parametros = parametros,
                            guardiaId = guardiaId,
                            onSuccess = onSuccessHandler,
                            onFailure = onFailureHandler
                        )
                    }
                }
            }
        }
    )

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
    Label_Id: String = stringResource(R.string.Value_Default_Label_Id),
    Tipo_Report: String = stringResource(R.string.Name_Minuta_Ele),
    guardiaId: String
) {
    var id by remember { mutableStateOf<Long?>(null) }
    var placa by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    // Ahora usamos una lista, aunque solo contendrá 0 o 1 elemento
    var evidenciasUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    if (Tipo_Report == "Elemento") {
        ImagePicker(
            selectedUris = evidenciasUris,
            onImagesSelected = { uris -> evidenciasUris = uris },
            allowMultiple = false, // MODO IMAGEN ÚNICA
            label = stringResource(R.string.Value_Label_Element)
        )
    }
    if(Tipo_Report == "Vehicular"){
        CustomTextField(
            value = placa,
            label = Label_Id,
            onValueChange = { newString ->
                val filtered = newString.filter { it.isLetterOrDigit() }.lowercase()
                if (filtered.length <= 6) {
                    val letters = filtered.take(3)
                    val numbers = filtered.drop(3)

                    // Solo actualiza el estado si el contenido es válido
                    if (letters.all { it.isLetter() } && numbers.all { it.isDigit() }) {
                        placa = filtered // Guarda "ABC123", no "ABC-123"
                    }
                }
            },
            visualTransformation = PlacaVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )
        )
    }else{
        CustomTextField(
            value = id?.toString() ?: "",
            label = Label_Id,
            onValueChange = { newString ->
                val filteredString = newString.filter { it.isDigit() }
                id = if (filteredString.isNotEmpty()) filteredString.toLongOrNull() else null
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            )
        )
    }

    Space()

    CustomTextField(
        value = name,
        label = stringResource(R.string.Label_Nombre_Report),
        onValueChange = { newValue ->
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

    val id_placa = if (Tipo_Report == "Vehicular") {
        // Re-construimos el formato final ANTES de pasarlo al siguiente componente
        val letters = placa.take(3)
        val numbers = placa.drop(3)
        if (numbers.isNotEmpty()) "$letters-$numbers" else letters
    } else {
        id?.toString() ?: ""
    }

    val onResetAction: (Boolean) -> Unit = { shouldHold ->
        evidenciasUris = emptyList()

        if (Tipo_Report == "Elemento") {
            if (!shouldHold) {
                // --- Reseteamos 'id' a null ---
                id = null
                name = ""
            }
        } else {
            // --- Reseteamos 'id' a null ---
            id = null
            name = ""
            placa = ""
        }
    }
    // Pasamos la primera (y única) URI a Components_Template
    Components_Template(
        id = id_placa,
        name = name,
        tipo_report = Tipo_Report,
        evidenciasUri = evidenciasUris.firstOrNull(), // Puede ser nulo
        guardiaId = guardiaId,
        onResetFields = onResetAction
    )
}