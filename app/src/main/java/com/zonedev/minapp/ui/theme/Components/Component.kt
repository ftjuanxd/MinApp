package com.zonedev.minapp.ui.theme.Components


import android.net.Uri
import androidx.annotation.ColorRes
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Model.Reporte
import com.zonedev.minapp.ui.theme.Screen.Element
import com.zonedev.minapp.ui.theme.Screen.Observations
import com.zonedev.minapp.ui.theme.Screen.Personal
import com.zonedev.minapp.ui.theme.Screen.ProfileScreen
import com.zonedev.minapp.ui.theme.Screen.ScreenReport
import com.zonedev.minapp.ui.theme.Screen.Vehicular
import com.zonedev.minapp.ui.theme.ViewModel.GuardiaViewModel
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import com.zonedev.minapp.ui.theme.text

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

    // Variables dinámicas para el contenido del Navbar
    var title by remember { mutableStateOf(R.string.Descripcion_Navbar_Icon_Profile_Screen) }
    var logoIcon by remember { mutableStateOf(R.drawable.power_off) }
    var homeIcon = R.drawable.logo_home
    var fontSizeTitule by remember { mutableStateOf(25.sp) }
    var SizeIcon by remember { mutableStateOf(40.dp) }
    var previousPage by remember { mutableStateOf("home") }

    // Actualizamos los valores de Navbar según la opción seleccionada
    when (opcClic) {
        "obs" -> {
            title = R.string.Name_Interfaz_Observations
            logoIcon = homeIcon
            previousPage = "obs"
        }
        "veh" -> {
            title = R.string.Name_Interfaz_Vehicular
            logoIcon = homeIcon
            previousPage = "veh"
        }
        // Agregamos las otras opciones de la misma forma
        "home" -> {
            title = R.string.Descripcion_Navbar_Icon_Profile_Screen
            logoIcon = R.drawable.power_off
        }
        "per" -> {
            title = R.string.Name_Interfaz_Pedestrian_Access
            logoIcon =  homeIcon
            previousPage = "per"
        }
        "ele" -> {
            title = R.string.Name_Interfaz_Element
            logoIcon =  homeIcon
            previousPage = "ele"
        }
        "rep" -> {
            title = R.string.Name_Interfaz_Report
            logoIcon =  homeIcon
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
                home_power = logoIcon,
                fontSizeTitule = fontSizeTitule,
                SizeIcon = SizeIcon,
                onMenuClick = { isSidebarVisible = !isSidebarVisible },
                onItemClick = { clickedOption ->
                    opcClic = clickedOption
                },
                navController,
                previousPage = previousPage
            )
            Space(50.dp)
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (opcClic) {
                    "home" -> ProfileScreen(guardiaViewModel)
                    "obs" -> Observations(guardiaId)
                    "veh" -> Vehicular(guardiaId)
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
fun Separator() {
    HorizontalDivider(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(top = 10.dp),
        thickness = 1.dp,
        color = Color.Gray
    )
}

@Composable
fun Space(height: Dp = 8.dp){
    Spacer(modifier= Modifier.height(height))
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
    @ColorRes iconTint: Int? = null,
    pdHeight: Dp? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isUser: Boolean? = null,
    isPasswordField: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Determina la alineación según isUser
    val alignmentModifier = when (isUser) {
        true -> Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
        false -> Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
        else -> Modifier.fillMaxWidth()
    }

    // Determina el color final que se usará para el tinte del icono
    val resolvedIconTint = when {
        iconTint != null -> colorResource(id = iconTint) // Si se proporciona un ID de recurso, úsalo
        else -> Color.Black // Por defecto si no se proporciona ninguno
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
            if (isPasswordField)
            {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                Icon(
                    imageVector = image,
                    contentDescription = description,
                    tint = resolvedIconTint,
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            } else if (trailingIcon != null) {
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    tint = resolvedIconTint
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextFieldCamera(
    label: String,
    onClick: () -> Unit,
    imageUri: Uri? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(100.dp)
            .clickable { onClick()},
    ) {
        TextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label,color = color_component) },
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, primary, RoundedCornerShape(12.dp)),
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
        val hasImage = imageUri != null && imageUri.toString().isNotEmpty()

        Image(
            painter = if (hasImage)
                rememberAsyncImagePainter(imageUri)
            else
                painterResource(R.drawable.ic_image),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(120.dp)
                .padding(6.dp)
                .alpha(0.3f)
        )
    }
}

@Composable
fun Navbar(
    @StringRes Titule: Int,
    @DrawableRes home_power: Int,
    fontSizeTitule: TextUnit,
    SizeIcon: Dp,
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
            text = stringResource(id = Titule),
            color = background,
            fontSize = fontSizeTitule,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(start = 8.dp) // Add some padding between icon and text
                .weight(1f) // Allows Text to take available space
                .padding(top = 2.dp)
        )
        Row {
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
@Preview
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
        //Space(8.dp)
        Text(
            text = stringResource(R.string.Name_CheckHolder),
            fontSize = 15.sp,
            modifier = Modifier.padding(all = 8.dp)
        )
    }

    // Retornar el estado observable de isChecked
    return isChecked
}

@Composable
fun FieldsThemes(destiny:String,onDestinyChange: (String) -> Unit,auto:String,onAutoChange: (String) -> Unit,descrip:String,onDescripChange: (String) -> Unit){

    CustomTextField(
        value = destiny,
        label = "Destino",
        onValueChange = onDestinyChange,
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    Space()
    //TextField Authorization
    CustomTextField(
        value = auto,
        label = "Autorizacion",
        onValueChange = onAutoChange,
        isEnabled = true,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
    )
    Space()
    //TextField Description
    CustomTextField(
        value = descrip,
        label = "Descripcion",
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
            Space(10.dp)
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
            Space(10.dp)
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
            Space(10.dp)
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
            Space(10.dp)
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
        }
    }
}