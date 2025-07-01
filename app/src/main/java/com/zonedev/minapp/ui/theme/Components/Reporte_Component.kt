package com.zonedev.minapp.ui.theme.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.zonedev.minapp.ui.theme.Model.Reporte
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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

    // Actualizar los reportes cada vez que cambian los filtros
    LaunchedEffect(idFiltro, nombreFiltro, tipoFiltro, fechaInicio, fechaFin) {
        reportes = reporteViewModel.buscarReportes(
            guardiaId = guardiaId,
            id = idFiltro,
            nombre = nombreFiltro,
            tipo = tipoFiltro,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
        println(idFiltro)
    }

    // Filtros de búsqueda
    Column(modifier = Modifier.padding(10.dp)) {
        // Filtro por Tipo
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            var expandedTipo by remember { mutableStateOf(false) }
            var selectedTipo by remember { mutableStateOf(tipoFiltro) }

            ButtonApp(
                text = "$selectedTipo",
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
                            selectedTipo = tipo
                            tipoFiltro = tipo
                            expandedTipo = false
                        },
                        text = { Text(text = tipo) }
                    )
                }
            }
        }

        Space(12.dp)

        CustomTextField(
            value = idFiltro,
            label = "ID del Usuario",
            onValueChange = { idFiltro = it },
            modifier = Modifier.fillMaxWidth()
        )

        Space(12.dp)

        CustomTextField(
            value = nombreFiltro,
            label = "Nombre del Usuario",
            onValueChange = { nombreFiltro = it },
            modifier = Modifier.fillMaxWidth()
        )

        Space(12.dp)
    }
    // Mostrar los reportes filtrados
    Space(20.dp)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PaginationScreen(reportes)
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

    Column {
        Pagination(
            totalPages = totalPages,
            currentPage = currentPage,
            onPageChanged = { newPage -> currentPage = newPage }
        )

        Space(10.dp)
        ContentForPage(reportes = reportes, itemsPerPage = itemsPerPage, currentPage = currentPage)

        Space(16.dp)
    }
}

// Función para formatear la fecha a "dd/MM/yyyy"

private fun formatDate(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let { date ->
        // Define el formato deseado para la fecha
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.format(date)
    } ?: ""
}

// Filtro de Fecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDatePickerTextField(
    label: String,
    selectedDate: Timestamp?,
    onDateSelected: (Timestamp) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado para controlar la visibilidad del diálogo del selector de fecha
    var showDialog by remember { mutableStateOf(false) }

    // El texto que se mostrará en el TextField, formateado a partir de la fecha seleccionada
    val formattedDate = formatDate(selectedDate)

    // Componente OutlinedTextField que actúa como un botón para abrir el selector de fecha
    OutlinedTextField(
        value = formattedDate,
        onValueChange = { /* No se permite la edición manual, el valor se actualiza con el selector */ },
        modifier = modifier.fillMaxWidth(), // Asegura que el campo de texto ocupe todo el ancho disponible
        label = { Text(label) },
        readOnly = true, // Impide que el usuario escriba directamente en el campo
        trailingIcon = {
            // Icono de calendario que al hacer clic abre el selector de fecha
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha" // Descripción para accesibilidad
                )
            }
        }
    )

    // Si showDialog es true, se muestra el DatePickerDialog
    if (showDialog) {
        // Estado del selector de fecha, inicializado con la fecha seleccionada o la fecha actual
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.toDate()?.time ?: System.currentTimeMillis()
        )

        // Diálogo del selector de fecha
        DatePickerDialog(
            onDismissRequest = { showDialog = false }, // Cierra el diálogo al hacer clic fuera o presionar atrás
            confirmButton = {
                TextButton(
                    onClick = {
                        // Solo si el usuario ha seleccionado una fecha (no es nula)
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Convertimos los milisegundos a un objeto Calendar
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = millis
                                // Aseguramos que la hora sea el inicio del día (00:00:00)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            // Invocamos el callback con el nuevo Timestamp de Firebase
                            onDateSelected(Timestamp(calendar.time))
                        }
                        showDialog = false // Cierra el diálogo después de la selección
                    }
                ) {
                    Text("Aceptar") // Texto del botón de confirmación
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar") // Texto del botón de cancelación
                }
            }
        ) {
            // El componente DatePicker real dentro del diálogo
            DatePicker(state = datePickerState)
        }
    }
}