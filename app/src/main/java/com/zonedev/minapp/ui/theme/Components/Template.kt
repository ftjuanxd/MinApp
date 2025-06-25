package com.zonedev.minapp.ui.theme.Components


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

/**
@Composable
fun Template_Scan(IsScreenElement: Boolean=false,vals:String = stringResource(R.string.Value_Default_Label_Camera),guardiaId: String){

    if (IsScreenElement){
        //Camara de elementos
        CaptureImageScreen(vals){
            base64Image -> // Manejar la imagen capturada
        }
        //Camara de Identificacion
        CaptureImageScreen(){
            base64Image -> // Manejar la imagen capturada
        }
        //Componentes
        Components_Template(guardiaId = guardiaId)
    }else{
        //Camara de Elementos
        CaptureImageScreen(vals){
            base64Image -> // Manejar la imagen capturada
        }
        //Componentes
        Components_Template(guardiaId = guardiaId)
    }
}**/

@Composable
fun Template_Text(
    IsScreenElement: Boolean = false,
    Label_Id: String = stringResource(R.string.Value_Default_Label_Id),
    Tipo_Report: String = "Elemento",
    guardiaId: String
) {
    // Variables de los textfields
    var Id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    /**if (IsScreenElement) {
        CaptureImageScreen(stringResource(R.string.Value_Label_Element)) { base64Image ->
            // Manejar la imagen capturada
        }
    }**/

    CustomTextField(
        value = Id,
        label = Label_Id,
        onValueChange = { Id = it },
        isEnabled = true,
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
        isEnabled = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )

    Space(8.dp)

    // Pasamos una lambda para resetear los valores de Id y name
    Components_Template(Id, name, Tipo_Report, guardiaId) {
        Id = ""
        name = ""
    }
}

@Composable
fun Components_Template(
    Id: String = "",
    name: String = "",
    tipo_report: String = "Elemento",
    guardiaId: String,
    reporteViewModel: ReporteViewModel = viewModel(),
    onResetFields: (() -> Unit)? = null // Lambda para resetear los valores
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
        // --- LÓGICA DE VERIFICACIÓN ---
        // Se comprueba que ningún campo esencial esté vacío antes de continuar.
        val isFormValid = Id.isNotBlank() && name.isNotBlank() &&
                destiny.isNotBlank() && auto.isNotBlank() && descrip.isNotBlank()

        if (isFormValid) {
            // Si el formulario es válido, muestra el diálogo de confirmación.
            showConfirmationDialog = true
        } else {
            // Si es inválido, muestra un diálogo de error.
            showValidationErrorDialog = true
        }
    }

    Separetor()

    // --- DIÁLOGO DE CONFIRMACIÓN (SOLO SE MUESTRA SI LA VALIDACIÓN ES EXITOSA) ---
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.Name_Modal_Report),
                    color = colorResource(id=R.color.primary),
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
                ButtonApp( // Usando Button de Material3 para consistencia
                    onClick = {
                        // La lógica para crear el reporte se mantiene aquí,
                        // ya que solo se ejecuta tras la confirmación del usuario.
                        val datos = if (tipo_report != "Elemento") {
                            mapOf(
                                "Id_placa" to Id.lowercase(),
                                "Name" to name.lowercase(),
                                "Destino" to destiny.lowercase(),
                                "Autorizacion" to auto.lowercase(),
                                "Descripcion" to descrip.lowercase()
                            )
                        } else {
                            mapOf(
                                "Imgelement" to "", // Considerar cómo se maneja la imagen
                                "Id_placa" to Id.lowercase(),
                                "Name" to name.lowercase(),
                                "Destino" to destiny.lowercase(),
                                "Autorizacion" to auto.lowercase(),
                                "Descripcion" to descrip.lowercase()
                            )
                        }

                        val parametros = crearParametrosParaReporte(tipo_report, datos)
                        reporteViewModel.crearReporte(tipo_report, parametros, guardiaId)

                        // Lógica para resetear los campos
                        if (holdCheckState.value) {
                            onResetFields?.invoke()
                        } else {
                            destiny = ""
                            auto = ""
                            descrip = ""
                            onResetFields?.invoke()
                        }
                        showConfirmationDialog = false
                    }, text = stringResource(id=R.string.Value_Button_Report)
                )
            }
        )
    }

    // --- DIÁLOGO DE ERROR DE VALIDACIÓN ---
    if (showValidationErrorDialog) {
        AlertDialog(
            onDismissRequest = { showValidationErrorDialog = false },
            title = { Text(
                text="Campos Incompletos",
                color= primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                )
                Space(8.dp)
            },

            text = { Text(
                text="Por favor, asegúrese de llenar todos los campos antes de generar el reporte.",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 6.dp))
            },
            confirmButton = {
                ButtonApp (
                    text=stringResource(id=R.string.Value_Button_Report),
                    onClick = { showValidationErrorDialog = false }
                )
            }
        )
    }
}
