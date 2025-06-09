package com.zonedev.minapp.ui.theme.Components


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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

    CustomTextField(
        value = name,
        label = "Name",
        onValueChange = { name = it },
        isEnabled = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )

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

    var showDialog by remember { mutableStateOf(false) }

    val holdCheckState = CheckHold() // Esto ahora tiene el estado observable

    FieldsThemes(destiny, { destiny = it }, auto, { auto = it }, descrip, { descrip = it })

    Separetor()

    ButtonApp(stringResource(R.string.button_submit)) { showDialog = true }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.Name_Modal_Report),
                    color = primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    text = stringResource(R.string.Value_Button_Report),
                    onClick = {
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
                                "Imgelement" to "",
                                "Id_placa" to Id.lowercase(),
                                "Name" to name.lowercase(),
                                "Destino" to destiny.lowercase(),
                                "Autorizacion" to auto.lowercase(),
                                "Descripcion" to descrip.lowercase()
                            )
                        }

                        val parametros = crearParametrosParaReporte(tipo_report, datos)
                        reporteViewModel.crearReporte(tipo_report, parametros, guardiaId)

                        // Luego puedes acceder a holdCheckState.value para ver si el checkbox está marcado o no.
                        if (holdCheckState.value) {
                            onResetFields?.let { it() }
                            // Aquí haces lo que necesites cuando esté marcado
                        } else {
                            // Aquí haces lo que necesites cuando esté desmarcado
                            //img = ""
                            destiny = ""
                            auto = ""
                            descrip = ""
                            onResetFields?.let { it() }
                        }
                        showDialog = false
                    }
                )
            }
        )
    }
}
