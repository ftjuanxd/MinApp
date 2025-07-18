package com.zonedev.minapp.ui.Templates

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.Components.Space
import com.zonedev.minapp.ui.Screen.Guardia.Element
import com.zonedev.minapp.ui.Screen.Guardia.Observations
import com.zonedev.minapp.ui.Screen.Guardia.Personal
import com.zonedev.minapp.ui.Screen.Guardia.Vehicular
import com.zonedev.minapp.ui.Screen.ProfileScreen
import com.zonedev.minapp.ui.Screen.ScreenReport
import com.zonedev.minapp.ViewModel.GuardiaViewModel
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.primary

@Composable
fun BaseScreen(
    opc: String = "home",
    navController: NavController,
    guardiaViewModel: GuardiaViewModel,
    guardiaId: String
) {
    var opcClic by remember { mutableStateOf(opc) }
    var isSidebarVisible by remember { mutableStateOf(false) }

    //  Añadimos un BackHandler para controlar el botón de retroceso.
    //  Se activa solo si NO estamos en la pantalla "home"
    BackHandler(enabled = opcClic != "home"){
        //Si se presiona "atras" desde el celular sino se esta en la pantalla de home se vuelve a home
        opcClic = "home"
        isSidebarVisible= false // en caso de que esté abierto el sidebar lo cerramos
    }

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
            if (opcClic == "rep"){
                Space(8.dp)
            }else{
                Space(25.dp)
            }

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

