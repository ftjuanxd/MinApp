package com.zonedev.minapp.ui.Components.Report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.zonedev.minapp.Model.Reporte
import com.zonedev.minapp.R
import com.zonedev.minapp.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.Components.ButtonApp
import com.zonedev.minapp.ui.Components.CustomTextField
import com.zonedev.minapp.ui.Components.Space
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import com.zonedev.minapp.ui.theme.text
import com.zonedev.minapp.util.ReportViewTestTags
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.min

@Composable
fun DropdownMenu(guardiaId: String, reporteViewModel: ReporteViewModel = viewModel()) {
    var selectedOption by remember { mutableStateOf("Personal") }
    val options = listOf("Personal", "Vehicular", "Elemento", "Observacion")
    var reportes by remember { mutableStateOf(emptyList<Reporte>()) }
    //--- VARIABLES FILTRO ---
    var idFiltro by remember { mutableStateOf("") }
    var nombreFiltro by remember { mutableStateOf("") }
    var tipoFiltro by remember { mutableStateOf(selectedOption) }
    var fechaInicio by remember { mutableStateOf<Timestamp?>(null) }
    var fechaFin by remember { mutableStateOf<Timestamp?>(null) }
    // Variables para el filtro de guarda/nombre
    var guardiasDelPuesto by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var filtroGuardiaId by remember { mutableStateOf<String?>(null) }
    var nombreGuardiaSeleccionado by remember { mutableStateOf("Todos los guardias") }
    // Variables para las paginas de los reportes
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 10
    // Variables de Estado para el muestreo del contenido de los reportes
    var showDialog by remember { mutableStateOf(false) }
    var selectedReporte by remember { mutableStateOf<Reporte?>(null) }

    LaunchedEffect(tipoFiltro) {
        idFiltro = ""
        nombreFiltro = ""
        fechaInicio = null
        fechaFin = null
        currentPage = 1
    }

    LaunchedEffect(guardiaId) {
        guardiasDelPuesto = reporteViewModel.obtenerGuardiasDelPuesto(guardiaId)
    }

    LaunchedEffect(idFiltro, nombreFiltro, tipoFiltro, fechaInicio, fechaFin, filtroGuardiaId) {
        reportes = reporteViewModel.buscarReportesPorPuesto(
            guardiaIdActual = guardiaId,
            id = idFiltro,
            nombre = nombreFiltro,
            tipo = tipoFiltro,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            filtroGuardiaId = filtroGuardiaId
        )
        currentPage = 1
    }
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp),horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    var expandedTipo by remember { mutableStateOf(false) }

                    ButtonApp(
                        text = tipoFiltro,
                        iconButton = true,
                        onClick = { expandedTipo = true },
                        modifier = Modifier.testTag(ReportViewTestTags.DROPDOWN_BUTTON)
                    )

                    DropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .testTag(ReportViewTestTags.DROPDOWN_MENU)
                    ) {
                        options.forEach { tipo ->
                            DropdownMenuItem(
                                onClick = {
                                    tipoFiltro = tipo
                                    expandedTipo = false
                                },
                                text = { Text(text = tipo) },
                                modifier = Modifier.testTag(ReportViewTestTags.dropdownItem(tipo))
                            )
                        }
                    }
                }

                Space()
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    var expandedGuardias by remember { mutableStateOf(false) }

                    ButtonApp(
                        text = nombreGuardiaSeleccionado,
                        iconButton = true,
                        onClick = { expandedGuardias = true }
                    )

                    DropdownMenu(
                        expanded = expandedGuardias,
                        onDismissRequest = { expandedGuardias = false },
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        // Opción para limpiar el filtro y ver todos los reportes
                        DropdownMenuItem(
                            onClick = {
                                filtroGuardiaId = null
                                nombreGuardiaSeleccionado = "Todos los guardias"
                                expandedGuardias = false
                            },
                            text = { Text(text = "Todos los guardias") }
                        )
                        // Opciones para cada guardia en el puesto
                        guardiasDelPuesto.forEach { (id, nombre) ->
                            DropdownMenuItem(
                                onClick = {
                                    filtroGuardiaId = id
                                    nombreGuardiaSeleccionado = nombre
                                    expandedGuardias = false
                                },
                                text = { Text(text = nombre) }
                            )
                        }
                    }
                }
                Space()
            }
            item {
                when (tipoFiltro) {
                    "Observacion" -> {
                        CustomTextField(
                            value = nombreFiltro,
                            label = stringResource(R.string.Label_Filtro_Obs_Report),
                            onValueChange = { nombreFiltro = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            text_Tag = ReportViewTestTags.OBSERVATION_TITLE_FILTER_FIELD
                        )
                    }

                    "Vehicular" -> {
                        // Filtro para Placa: Acepta letras, números y guiones.
                        CustomTextField(
                            value = idFiltro,
                            label = stringResource(R.string.Label_Filtro_Veh_Report),
                            onValueChange = { newValue ->
                                // Esta validación asegura que el String `idFiltro` solo contenga
                                // los caracteres permitidos para una placa.
                                if (newValue.all { it.isLetterOrDigit() || it == '-' }) {
                                    idFiltro = newValue.lowercase()
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            text_Tag = ReportViewTestTags.ID_FILTER_FIELD
                        )
                        Space()
                        // --- Filtro para Nombre (letras y espacios) ---
                        CustomTextField(
                            value = nombreFiltro,
                            label = stringResource(R.string.Label_Filtro_User_Name_Report),
                            onValueChange = { newValue ->
                                if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                                    nombreFiltro = newValue
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            text_Tag = ReportViewTestTags.NAME_FILTER_FIELD,
                        )
                    }

                    "Personal", "Elemento" -> {
                        // Filtro para ID: Acepta solo números.
                        CustomTextField(
                            value = idFiltro,
                            label = stringResource(R.string.Label_Filtro_User_ID_Report),
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    idFiltro = newValue
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                            ),
                            text_Tag = ReportViewTestTags.ID_FILTER_FIELD
                        )
                        Space()
                        // --- Filtro para Nombre (letras y espacios) ---
                        CustomTextField(
                            value = nombreFiltro,
                            label = stringResource(R.string.Label_Filtro_User_Name_Report),
                            onValueChange = { newValue ->
                                if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                                    nombreFiltro = newValue
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            text_Tag = ReportViewTestTags.NAME_FILTER_FIELD,
                        )
                    }
                }
                Space()
            }
            item {
                ModernDatePickerTextField(
                    label = stringResource(R.string.Label_Filtro_Fecha_Inicio_Report),
                    selectedDate = fechaInicio,
                    onDateSelected = { fechaInicio = it },
                    onDateCleared = { fechaInicio = null },
                    modifier = Modifier.testTag(ReportViewTestTags.START_DATE_FIELD)
                )
                Space()
                ModernDatePickerTextField(
                    label = stringResource(R.string.Label_Filtro_Fecha_Fin_Report),
                    selectedDate = fechaFin,
                    onDateSelected = { fechaFin = it },
                    onDateCleared = { fechaFin = null },
                    modifier = Modifier.testTag(ReportViewTestTags.END_DATE_FIELD)
                )
                Space(12.dp)
            }
            // --- PAGINACIÓN ---
            val totalPages = (reportes.size + itemsPerPage - 1) / itemsPerPage
            if (totalPages > 1) {
                item {
                    Pagination(
                        totalPages = totalPages,
                        currentPage = currentPage,
                        onPageChanged = { newPage -> currentPage = newPage }
                    )
                }
            }
            // --- LISTA DE RESULTADOS ---
            val startIndex = (currentPage - 1) * itemsPerPage
            val endIndex = min(startIndex + itemsPerPage, reportes.size)
            val reportesEnPagina =
                if (startIndex <= endIndex) reportes.subList(startIndex, endIndex) else emptyList()

            if (reportesEnPagina.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.Mensaje_Reporte_No_Encontrado),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(reportesEnPagina, key = { it.hashCode() }) { reporte ->
                    val clave = obtenerClavePorTipo(reporte.tipo)
                    val nombreMostrado = obtenerParametro(reporte, clave)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            // ✅ **CAMBIO CLAVE**: El clickable ahora actualiza el estado local.
                            .clickable {
                                selectedReporte = reporte
                                showDialog = true
                            }
                    ) {
                        Text(text = nombreMostrado, modifier = Modifier.padding(16.dp))

                    }
                }
            }
        }
        if (showDialog && selectedReporte != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Detalle del Reporte") },
                text = {
                    LazyColumn { // Usar LazyColumn dentro del diálogo por si el contenido es largo
                        item {
                            MostrarReporte(reporte = selectedReporte!!, tipo = selectedReporte!!.tipo)
                        }
                    }
                },
                confirmButton = {
                    ButtonApp(
                        text = "CERRAR",
                        onClick = { showDialog = false }
                    )
                }
            )
        }
    }
}

private fun formatDate(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let { date ->
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.format(date)
    } ?: ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDatePickerTextField(
    label: String,
    selectedDate: Timestamp?,
    onDateSelected: (Timestamp) -> Unit,
    onDateCleared: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val formattedDate = formatDate(selectedDate)

    TextField(
        value = formattedDate,
        onValueChange = {},
        label = { Text(text = label, color = color_component) },
        readOnly = true,
        enabled = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(2.dp, primary, RoundedCornerShape(12.dp))
            .clickable { showDialog = true },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedDate != null) {
                    IconButton(onClick = onDateCleared) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.Clean_Filter_Fecha),
                            tint = colorResource(id= R.color.color_component)
                        )
                    }
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.Select_Filter_Fecha),
                        tint = colorResource(id= R.color.color_component)

                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            // Colores del texto que ya tenías
            focusedTextColor = text,
            unfocusedTextColor = text,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,

            // Define el color del contenedor para cada estado
            focusedContainerColor = background,
            unfocusedContainerColor = background,
            disabledContainerColor = background,

            // Mantén los indicadores transparentes como lo tenías
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent, // Es buena práctica definir también el de error

            // Color de la etiqueta (label) que ya tenías
            disabledLabelColor = Color.Transparent
        )
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.toDate()?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                            calendar.timeInMillis = millis
                            onDateSelected(Timestamp(calendar.time))
                        }
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.Value_Button_Date_Ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.Value_Button_Date_No))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun Pagination(
    totalPages: Int,
    currentPage: Int,
    onPageChanged: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color_component)
    ) {
        CompositionLocalProvider(LocalContentColor provides background){
            TextButton(
                modifier = Modifier.padding(2.dp),
                onClick = {
                    if (currentPage > 1) {
                        onPageChanged(currentPage - 1)
                    }
                },
                enabled = currentPage > 1
            ) {
                Text(stringResource(R.string.Value_Pagination_Previo), color = background)
            }

            for (page in 1..totalPages) {
                TextButton(
                    modifier = Modifier.padding(2.dp),
                    onClick = {
                        onPageChanged(page)
                    }
                ) {
                    Text(
                        text = page.toString(),
                        color = background
                    )
                }
            }

            TextButton(
                modifier = Modifier.padding(2.dp),
                onClick = {
                    if (currentPage < totalPages) {
                        onPageChanged(currentPage + 1)
                    }
                },
                enabled = currentPage < totalPages
            ) {
                Text(stringResource(R.string.Value_Pagination_Siguiente), color = background)
            }
        }
    }
}