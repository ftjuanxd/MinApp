package com.zonedev.minapp.ui.theme.Components


import android.app.DatePickerDialog
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Model.Reporte
import com.zonedev.minapp.ui.theme.Screen.Chat
import com.zonedev.minapp.ui.theme.Screen.Element
import com.zonedev.minapp.ui.theme.Screen.Observations
import com.zonedev.minapp.ui.theme.Screen.Personal
import com.zonedev.minapp.ui.theme.Screen.ProfileScreen
import com.zonedev.minapp.ui.theme.Screen.ScreenReport
import com.zonedev.minapp.ui.theme.Screen.Vehicular
import com.zonedev.minapp.ui.theme.ViewModel.GuardiaViewModel
import com.zonedev.minapp.ui.theme.ViewModel.ReporteViewModel
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import com.zonedev.minapp.ui.theme.text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BaseScreen(
    opc: String = "home",
    navController: NavController,
    guardiaViewModel: GuardiaViewModel,
    guardiaId: String
) {
    var opcClic by remember { mutableStateOf(opc) }
    var isSidebarVisible by remember { mutableStateOf(false) }

    // Variable para marcar si vienes desde "home"
    var isFromHome by remember { mutableStateOf(false) }

    // Variables dinámicas para el contenido del Navbar
    var title by remember { mutableStateOf(R.string.Descripcion_Navbar_Icon_Profile_Screen) }
    var notificationIcon by remember { mutableStateOf(R.drawable.notificacion) }
    var logoIcon by remember { mutableStateOf(R.drawable.power_off) }
    var fontSizeTitule by remember { mutableStateOf(20.sp) }
    var SizeIcon by remember { mutableStateOf(40.dp) }
    var endPadding by remember { mutableStateOf(180.dp) }
    var previousPage by remember { mutableStateOf("home") }

    // Actualizamos los valores de Navbar según la opción seleccionada
    when (opcClic) {
        "obs" -> {
            title = R.string.Name_Interfaz_Observations
            notificationIcon = R.drawable.notificacion
            logoIcon = R.drawable.logo_home
            fontSizeTitule = 20.sp
            SizeIcon = 40.dp
            endPadding = 100.dp
            previousPage = "obs"
        }
        "veh" -> {
            title = R.string.Name_Interfaz_Vehicular
            notificationIcon = R.drawable.notificacion
            logoIcon = R.drawable.logo_home
            fontSizeTitule = 25.sp
            SizeIcon = 40.dp
            endPadding = 130.dp
            previousPage = "veh"
        }
        // Agregamos las otras opciones de la misma forma
        "home" -> {
            title = R.string.Descripcion_Navbar_Icon_Profile_Screen
            notificationIcon = R.drawable.notificacion
            logoIcon = R.drawable.power_off
            fontSizeTitule = 20.sp
            SizeIcon = 40.dp
            endPadding = 180.dp
        }
        "chat" -> {
            title = R.string.Name_Interfaz_Chat
            notificationIcon = R.drawable.notificacion_disable
            logoIcon = R.drawable.logo_home
            fontSizeTitule = 20.sp
            SizeIcon = 40.dp
            endPadding = 200.dp
        }
        "per" -> {
            title = R.string.Name_Interfaz_Pedestrian_Access
            notificationIcon = R.drawable.notificacion
            logoIcon =  R.drawable.logo_home
            fontSizeTitule = 15.sp
            SizeIcon = 40.dp
            endPadding = 80.dp
            previousPage = "per"
        }
        "ele" -> {
            title = R.string.Name_Interfaz_Element
            notificationIcon = R.drawable.notificacion
            logoIcon =  R.drawable.logo_home
            fontSizeTitule = 25.sp
            SizeIcon = 40.dp
            endPadding = 130.dp
            previousPage = "ele"
        }
        "rep" -> {
            title = R.string.Name_Interfaz_Report
            notificationIcon = R.drawable.notificacion
            logoIcon =  R.drawable.logo_home
            fontSizeTitule = 20.sp
            SizeIcon = 40.dp
            endPadding = 200.dp
            previousPage = "rep"
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Navbar(
                Titule = title,
                Activenotificacion = notificationIcon,
                home_power = logoIcon,
                fontSizeTitule = fontSizeTitule,
                SizeIcon = SizeIcon,
                endPadding = endPadding,
                onMenuClick = { isSidebarVisible = !isSidebarVisible },
                onItemClick = { clickedOption ->
                    opcClic = clickedOption
                },
                navController,
                previousPage =  previousPage
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (opcClic) {
                    "home" -> ProfileScreen(guardiaViewModel)
                    "obs" -> Observations(guardiaId)
                    "veh" -> Vehicular(guardiaId)
                    "chat" -> Chat()
                    "per" -> Personal(guardiaId)
                    "ele" -> Element(guardiaId)
                    "rep" -> ScreenReport(guardiaId)
                }
            }
        }
        // Sidebar y su fondo
        if (isSidebarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 56.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isSidebarVisible = false }
            )
        }
        if (isSidebarVisible) {
            SideBar(
                isVisible = isSidebarVisible,
                onItemClick = { clickedOption ->
                    opcClic = clickedOption
                    isSidebarVisible = false
                }
            )
        }
    }
}

@Composable
fun Separetor() {
    Divider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
    @DrawableRes trailingIcon: Int? = null,
    iconTint: Color? = null,
    pdHeight: Dp? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    bitmap: Bitmap? = null,
    isUser: Boolean? = null,
    isPasswordField: Boolean = false // Nuevo parámetro para indicar si es un campo de contraseña
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Determina la alineación según isUser
    val alignmentModifier = when (isUser) {
        true -> Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
        false -> Modifier.fillMaxWidth().wrapContentWidth(Alignment.Start)
        else -> Modifier.fillMaxWidth()
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = color_component) },
        enabled = isEnabled,
        readOnly = onClick != null,
        modifier = alignmentModifier
            .padding(vertical = 8.dp)
            .border(2.dp, primary, RoundedCornerShape(12.dp))
            .let { if (pdHeight != null) it.height(pdHeight) else it }
            .clickable {
                onClick?.invoke()
            },
        visualTransformation = if (isPasswordField && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPasswordField) KeyboardOptions(keyboardType = KeyboardType.Password) else keyboardOptions,
        trailingIcon = {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            } else if (isPasswordField) {
                // Alterna entre "Mostrar" y "Ocultar"
                Text(
                    text = if (passwordVisible) "Ocultar" else "Mostrar",
                    color = iconTint ?: Color.Black,
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            } else if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    tint = iconTint ?: Color.Black
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledLabelColor = Color.Transparent,
            containerColor = background,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = text,
            focusedTextColor = text
        )
    )
}

@Composable
fun DatePickerWithCustomTextField(
    label: String,
    initialDate: com.google.firebase.Timestamp?,
    onDateSelected: (com.google.firebase.Timestamp) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Estado para almacenar la fecha seleccionada como texto
    val selectedDateText = remember { mutableStateOf(initialDate?.let { formatDate(it) } ?: "") }

    // CustomTextField que actúa como gatillo para abrir el DatePickerDialog
    CustomTextField(
        value = selectedDateText.value,
        label = label,
        onValueChange = { /* El valor no se modifica manualmente */ },
        onClick = {
            // Abrir el DatePickerDialog al hacer clic
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    // Actualizar el calendario con la fecha seleccionada
                    calendar.set(year, month, dayOfMonth, 0, 0, 0)
                    val timestamp = Timestamp(calendar.time)

                    // Actualizar el estado con la fecha seleccionada
                    selectedDateText.value = formatDate(timestamp)
                    // Enviar el valor de la fecha seleccionada a onDateSelected
                    onDateSelected(timestamp)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = modifier
    )
}

@Composable
fun Navbar(
    @StringRes Titule: Int,
    @DrawableRes Activenotificacion: Int,
    @DrawableRes home_power: Int,
    fontSizeTitule: TextUnit,
    SizeIcon: Dp,
    endPadding: Dp,
    onMenuClick: () -> Unit, // Para manejar el clic en el menú
    onItemClick: (String) -> Unit, // Manejar los clics de los ítems
    navController: NavController,
    previousPage : String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .background(primary)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo_menu_burger),
            contentDescription = stringResource(id = Titule),
            tint = colorResource(R.color.background),
            modifier = Modifier
                .size(30.dp)
                .clickable { onMenuClick() } // Abre/cierra el sidebar
        )
        Text(
            text = stringResource(id=Titule),
            color = background,
            fontSize = fontSizeTitule,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(end = endPadding, top = 5.dp)
        )
        Row {
            Icon(
                painter = painterResource(id = Activenotificacion),
                contentDescription = stringResource(R.string.Descripcion_Navbar_Icon_Notificacion),
                tint = colorResource(R.color.background),
                modifier = Modifier
                    .size(SizeIcon)
                    .clickable {
                        if (Activenotificacion == R.drawable.notificacion_disable) {
                            // Vuelve a la pantalla anterior
                            onItemClick(previousPage)
                        } else {
                            onItemClick("chat")
                        }
                    }
            )
            Icon(
                painter = painterResource(id = home_power),
                contentDescription = stringResource(R.string.Descripcion_Navbar_Icon_Power),
                modifier = Modifier
                    .size(SizeIcon)
                    .clickable {
                        if (home_power == R.drawable.power_off) {
                            navController.navigate("login")
                        } else {
                            onItemClick("home") // Ir al inicio
                        }
                    },
                tint = colorResource(R.color.background)
            )
        }
    }
}

@Composable
fun ButtonApp(
    text: String,
    iconButton: Boolean? = false,
    onClick: () -> Unit,
    //modifier: Modifier = Modifier solo si el diseno base no ocupa todo el espacio del modal
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 18.sp)
        if (iconButton == true) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.White,
            )
        }
    }
}

@Composable
fun SegmentedButton(ScanComponent: @Composable () -> Unit, TextComponent: @Composable () -> Unit) {
    // Estado que almacena qué botón está seleccionado (0 para Scan Id, 1 para Write)
    var selectedButton by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .border(5.dp, color_component, RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { selectedButton = 0 }, // Acción de seleccionar Scan Id
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            colors = if (selectedButton == 0) {
                // Si el botón está seleccionado, cambia el color
                ButtonDefaults.buttonColors(containerColor = color_component, contentColor = background)
            } else {
                // Si no está seleccionado, usa estos colores
                ButtonDefaults.buttonColors(containerColor = background, contentColor = color_component)
            },
            modifier = Modifier
                .weight(2f)//
        ) {
            Text(
                text = stringResource(R.string.Value_Default_Label_Camera),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = { selectedButton = 1 }, // Acción de seleccionar Write
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
            colors = if (selectedButton == 1) {
                ButtonDefaults.buttonColors(containerColor = color_component, contentColor = background)
            } else {
                ButtonDefaults.buttonColors(containerColor = background, contentColor = color_component)
            },
            modifier = Modifier
                .weight(2f)
        ) {
            Text(
                text = stringResource(R.string.Value_Segmented_Button),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)
        }
    }
    // Aquí se muestra el contenido según el botón seleccionado
    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los botones y el contenido
    when (selectedButton) {
        0 -> ScanComponent() // Mostrar contenido de Scan Id
        1 -> TextComponent()   // Mostrar contenido de Write
    }
}

@Composable
fun CheckHold(): MutableState<Boolean> {
    // Estado del Checkbox
    val isChecked = remember { mutableStateOf(false) }

    // Contenedor con el Checkbox y un Text para mostrar el estado
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 220.dp)
    ) {
        Box(
            modifier = Modifier
                .size(23.dp)  // Tamaño del checkbox
                .border(2.dp, primary, RoundedCornerShape(4.dp))  // Borde personalizado
                .padding(4.dp)  // Espacio entre el borde y el checkbox
        ) {
            Checkbox(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it }, // Actualiza el estado cuando se hace clic
                colors = CheckboxDefaults.colors(
                    checkedColor = primary,        // Color cuando está marcado
                    uncheckedColor = background,   // Color cuando está desmarcado
                    checkmarkColor = background    // Color del check
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el Checkbox y el texto
        Text(
            text = stringResource(R.string.Name_CheckHolder),
            fontSize = 15.sp
        )
    }

    // Retornar el estado observable de isChecked
    return isChecked
}


@Composable
fun FieldsThemes(destiny:String,onDestinyChange: (String) -> Unit,auto:String,onAutoChange: (String) -> Unit,descrip:String,onDescripChange: (String) -> Unit){

    CustomTextField(
        value = destiny,
        label = "Destiny",
        onValueChange = onDestinyChange,
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    //TextField Authorization
    CustomTextField(
        value = auto,
        label = "Authorization",
        onValueChange = onAutoChange,
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    //TextField Description
    CustomTextField(
        value = descrip,
        label = "Description",
        onValueChange = onDescripChange,
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        pdHeight = 80.dp
    )

}

@Composable
fun DropdownMenu(guardiaId: String, reporteViewModel: ReporteViewModel = viewModel()) {
    var selectedOption by remember { mutableStateOf("Personal") }
    val options = listOf("Personal", "Vehicular", "Elemento", "Observations")
    var reportes by remember { mutableStateOf(emptyList<Reporte>()) }
    var idFiltro by remember { mutableStateOf("") }
    var nombreFiltro by remember { mutableStateOf("") }
    var tipoFiltro by remember { mutableStateOf(selectedOption) }
    var fechaInicio by remember { mutableStateOf<com.google.firebase.Timestamp?>(null) }
    var fechaFin by remember { mutableStateOf<com.google.firebase.Timestamp?>(null) }

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

        Spacer(modifier = Modifier.height(12.dp))

        CustomTextField(
            value = idFiltro,
            label = "ID del Usuario",
            onValueChange = { idFiltro = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp ))

        CustomTextField(
            value = nombreFiltro,
            label = "Nombre del Usuario",
            onValueChange = { nombreFiltro = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    // Mostrar los reportes filtrados
    Spacer(modifier = Modifier.height(20.dp))
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PaginationScreen(reportes)
    }
}

// Función para convertir milisegundos a una fecha legible
fun formatDate(timestamp: com.google.firebase.Timestamp): String {
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date)
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

            //Spacer(modifier = Modifier.width(8.dp))

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

            //Spacer(modifier = Modifier.width(8.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

        ContentForPage(reportes = reportes, itemsPerPage = itemsPerPage, currentPage = currentPage)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
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
        for (index in startIndex until endIndex) {
            val reporte = reportes[index]

            // Obtener clave específica para este reporte
            val clave = obtenerClavePorTipo(reporte.tipo)

            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectedReporte = reporte
                    showDialog = true
                }
            ) {
                // Mostrar el valor específico de `parametros` según la clave
                Text(
                    text = obtenerParametro(reporte, clave),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    // Mostrar el modal solo si hay un reporte seleccionado
    if (showDialog && selectedReporte != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Detalle del Reporte") },
            text = {
                selectedReporte?.let { reporte ->
                    // Mostrar detalles del reporte seleccionado
                    LazyColumn {
                        item {
                            MostrarReporte(reporte,reporte.tipo)
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
fun SideBar(
    isVisible: Boolean,
    onItemClick: (String) -> Unit // Manejar los clics
) {
    val offsetX by animateDpAsState(
        targetValue = if (isVisible) 0.dp else (-178).dp, // Mostrar/ocultar sidebar
        animationSpec = tween(durationMillis = 300) // Animación suave
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .fillMaxHeight()
            .statusBarsPadding()
            .width(200.dp)
            .padding(top = 56.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(primary)
                .padding(top = 5.dp, start = 6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.logo_observations),
                contentDescription = null,
                tint = background,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onItemClick("obs") // Clic en "Observations"
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Icon(
                painter = painterResource(R.drawable.logo_vehicular),
                contentDescription = null,
                tint = background,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onItemClick("veh") // Clic en "Vehicular"
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Icon(
                painter = painterResource(R.drawable.logo_personal),
                contentDescription = null,
                tint = background,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onItemClick("per") // Clic en "Personal"
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Icon(
                painter = painterResource(R.drawable.logo_elements),
                contentDescription = null,
                tint = background,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onItemClick("ele") // Clic en "Elementos"
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Icon(
                painter = painterResource(R.drawable.logo_report),
                contentDescription = null,
                tint = background,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onItemClick("rep") // Clic en "Report"
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
