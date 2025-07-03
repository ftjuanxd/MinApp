package com.zonedev.minapp.ui.theme.Components.Report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Space
import com.zonedev.minapp.ui.theme.Model.Reporte
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import com.zonedev.minapp.ui.theme.text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun ContentForPage(reportes: List<Reporte>, itemsPerPage: Int, currentPage: Int) {
    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, reportes.size)

    var showDialog by remember { mutableStateOf(false) }
    var selectedReporte by remember { mutableStateOf<Reporte?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, color_component, shape = RoundedCornerShape(8.dp))
    ) {
        // --- INICIO DE LA MODIFICACIÓN ---
        if (reportes.isEmpty()) {
            // Si la lista está vacía, muestra el mensaje.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontró ningún reporte relacionado",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Si la lista tiene reportes, muestra los elementos.
            for (index in startIndex until endIndex) {
                val reporte = reportes[index]
                val clave = obtenerClavePorTipo(reporte.tipo)
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedReporte = reporte
                        showDialog = true
                    }
                ) {
                    Text(
                        text = obtenerParametro(reporte, clave),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    // El modal de detalles no cambia y funciona igual.
    if (showDialog && selectedReporte != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Detalle del Reporte") },
            text = {
                selectedReporte?.let { reporte ->
                    LazyColumn {
                        item {
                            MostrarReporte(reporte, reporte.tipo)
                        }
                    }
                }
            },
            confirmButton = {
                ButtonApp(
                    text = "Aceptar",
                    onClick = { showDialog = false }
                )
            }
        )
    }
}

@Composable
fun DropdownMenu(guardiaId: String, reporteViewModel: ReporteViewModel = viewModel()) {
    var selectedOption by remember { mutableStateOf("Personal") }
    val options = listOf("Personal", "Vehicular", "Elemento", "Observacion")
    var reportes by remember { mutableStateOf(emptyList<Reporte>()) }
    var idFiltro by remember { mutableStateOf("") }
    var nombreFiltro by remember { mutableStateOf("") }
    var tipoFiltro by remember { mutableStateOf(selectedOption) }
    var fechaInicio by remember { mutableStateOf<Timestamp?>(null) }
    var fechaFin by remember { mutableStateOf<Timestamp?>(null) }

    // Este efecto se ejecutará cada vez que el tipo de reporte cambie.
    LaunchedEffect(tipoFiltro) {
        // Resetea los otros filtros a su estado inicial.
        idFiltro = ""
        nombreFiltro = ""
        fechaInicio = null
        fechaFin = null
    }

    // Este efecto dispara la búsqueda cuando cualquier filtro cambia.
    LaunchedEffect(idFiltro, nombreFiltro, tipoFiltro, fechaInicio, fechaFin) {
        reportes = reporteViewModel.buscarReportes(
            guardiaId = guardiaId,
            id = idFiltro,
            nombre = nombreFiltro,
            tipo = tipoFiltro,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
    }

    // Filtros de búsqueda
    Column(modifier = Modifier.padding(10.dp)) {
        // Filtro por Tipo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            var expandedTipo by remember { mutableStateOf(false) }

            ButtonApp(
                text = tipoFiltro,
                iconButton = true,
            ) {
                expandedTipo = true
            }

            DropdownMenu(
                expanded = expandedTipo,
                onDismissRequest = { expandedTipo = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                options.forEach { tipo ->
                    DropdownMenuItem(
                        onClick = {
                            // Solo actualiza el tipo. El LaunchedEffect se encargará de resetear.
                            tipoFiltro = tipo
                            expandedTipo = false
                        },
                        text = { Text(text = tipo) }
                    )
                }
            }
        }

        Space()

        // --- LÓGICA DE UI CONDICIONAL REFACTORIZADA ---
        when (tipoFiltro) {
            "Observacion" -> {
                // Para Observaciones, solo mostramos un campo para buscar por título.
                // Usamos la variable 'nombreFiltro' para almacenar el título.
                CustomTextField(
                    value = nombreFiltro,
                    label = "Título de Observación",
                    onValueChange = { nombreFiltro = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            "Vehicular" -> {
                // Para Vehicular, mostramos campos para Placa y Nombre.
                CustomTextField(
                    value = idFiltro,
                    label = "Placa del Vehículo",
                    onValueChange = { idFiltro = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Space()
                CustomTextField(
                    value = nombreFiltro,
                    label = "Nombre del Usuario",
                    onValueChange = { nombreFiltro = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            "Personal", "Elemento" -> {
                // Para Personal y Elemento, mostramos campos para ID y Nombre.
                CustomTextField(
                    value = idFiltro,
                    label = "ID del Usuario",
                    onValueChange = { idFiltro = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Space()
                CustomTextField(
                    value = nombreFiltro,
                    label = "Nombre del Usuario",
                    onValueChange = { nombreFiltro = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Space()

        ModernDatePickerTextField(
            label = "Fecha de Inicio",
            selectedDate = fechaInicio,
            onDateSelected = { fechaInicio = it },
            onDateCleared = { fechaInicio = null } // Proporciona la lógica para limpiar
        )
        Space()

        ModernDatePickerTextField(
            label = "Fecha de Fin",
            selectedDate = fechaFin,
            onDateSelected = { fechaFin = it },
            onDateCleared = { fechaFin = null } // Proporciona la lógica para limpiar
        )
    }
    // Mostrar los reportes filtrados
    Space(12.dp)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PaginationScreen(reportes)
    }
}

// Función para formatear la fecha a "dd/MM/yyyy"
private fun formatDate(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let { date ->
        // Define el formato deseado para la fecha
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Establece la zona horaria a UTC para mostrar la fecha correctamente
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

    // Se utiliza TextField en lugar de OutlinedTextField para que coincida con CustomTextField
    TextField(
        value = formattedDate,
        onValueChange = {},
        label = { Text(text = label, color = color_component) }, // Se aplica el color de la etiqueta
        readOnly = true,
        enabled = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Se añade padding para consistencia
            .border(2.dp, primary, RoundedCornerShape(12.dp)) // Se aplica el mismo borde
            .clickable { showDialog = true }, // El campo completo es clickeable
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedDate != null) {
                    IconButton(onClick = onDateCleared) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar fecha",
                            tint = colorResource(id= R.color.color_component)
                        )
                    }
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = colorResource(id= R.color.color_component)

                    )
                }
            }
        },
        // Se aplican los mismos colores que en CustomTextField para un look unificado
        colors = TextFieldDefaults.textFieldColors(
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            containerColor = background,
            unfocusedTextColor = text,
            focusedTextColor = text
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
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
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
            .border(2.dp, primary, shape = RoundedCornerShape(8.dp))
            .background(primary)
    ) {
        CompositionLocalProvider(LocalContentColor provides background){
            // Botón de "Previous"
            TextButton(
                onClick = {
                    if (currentPage > 1) {
                        onPageChanged(currentPage - 1)
                    }
                },
                enabled = currentPage > 1
            ) {
                Text("Previous", color = if (currentPage > 1)  color_component else background)
            }

            // Números de páginas
            for (page in 1..totalPages) {
                TextButton(
                    onClick = {
                        onPageChanged(page)
                    }
                ) {
                    Text(
                        text = page.toString(),
                        color = if (page == currentPage) background else color_component
                    )
                }
            }

            // Botón de "Next"
            TextButton(
                onClick = {
                    if (currentPage < totalPages) {
                        onPageChanged(currentPage + 1)
                    }
                },
                enabled = currentPage < totalPages
            ) {
                Text("Next", color = if (currentPage < totalPages) color_component else background)
            }
        }

    }
}

@Composable
fun PaginationScreen(reportes: List<Reporte>) {
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 10
    val totalPages = (reportes.size + itemsPerPage - 1) / itemsPerPage

    // Reinicia la página actual si se vuelve inválida después de filtrar
    if (currentPage > totalPages && totalPages > 0) {
        currentPage = totalPages
    } else if (totalPages == 0) {
        currentPage = 1
    }

    Column {
        // Muestra los controles de paginación solo si hay reportes para paginar.
        if (totalPages > 0) {
            Pagination(
                totalPages = totalPages,
                currentPage = currentPage,
                onPageChanged = { newPage -> currentPage = newPage }
            )
        }

        Space(10.dp)
        // ContentForPage ahora maneja el mensaje de estado vacío.
        ContentForPage(reportes = reportes, itemsPerPage = itemsPerPage, currentPage = currentPage)

        Space(16.dp)
    }
}