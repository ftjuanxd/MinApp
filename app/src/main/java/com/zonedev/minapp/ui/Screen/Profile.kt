package com.zonedev.minapp.ui.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.CircularProgressIndicator
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.zonedev.minapp.R
import com.zonedev.minapp.ViewModel.GuardiaViewModel
import com.zonedev.minapp.ui.Components.ButtonApp
import com.zonedev.minapp.ui.Components.CustomTextField
import com.zonedev.minapp.ui.Components.Modal
import com.zonedev.minapp.ui.Components.Separator

@Composable
fun ProfileScreen(viewModel: GuardiaViewModel = viewModel()) {
    Components_Profile_Screen(viewModel)
}

@Composable
fun Components_Profile_Screen(guardiaViewModel: GuardiaViewModel = viewModel()){

    var showDialog by remember { mutableStateOf(false) }
    val guardia by guardiaViewModel.listaGuardias.collectAsState()
    val puestoNombre by guardiaViewModel.nombrePuesto.collectAsState()


    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen de perfil usando Coil
        guardia.firstOrNull()?.image?.let { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(R.string.Descripcion_profileScreen_Image),
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 18.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator()
                            println("Cargando imagen...${painter.state}")
                        }
                        is AsyncImagePainter.State.Error -> {
                            // Imagen por defecto en caso de error
                            Image(
                                painter = painterResource(id = R.drawable.logo_user_sample),
                                contentDescription = stringResource(R.string.Error_Image),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            } else {
                // Imagen por defecto si no hay URL
                Image(
                    painter = painterResource(id = R.drawable.logo_user_sample),
                    contentDescription = stringResource(R.string.Descripcion_profileScreen_Image),
                    modifier = Modifier
                        .size(160.dp)
                        .padding(bottom = 24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Separator()

        CustomTextField(
            value = guardia.firstOrNull()?.name ?: stringResource(R.string.Dato_No_Obtenido),
            label = stringResource(R.string.Label_Name_Profile),
            onValueChange = {},
            isEnabled = false,
        )
        CustomTextField(
            value = puestoNombre,
            label = stringResource(R.string.puesto_trabajo),
            onValueChange = {},
            isEnabled = false,
        )
        CustomTextField(
            value = guardia.firstOrNull()?.phone ?: stringResource(R.string.Dato_No_Obtenido),
            label = stringResource(R.string.Label_Phone_Profile),
            onValueChange = {},
            isEnabled = false,
        )
        CustomTextField(
            value = guardia.firstOrNull()?.id ?: stringResource(R.string.Dato_No_Obtenido),
            label = stringResource(R.string.Label_ID_Profile),
            onValueChange = {},
            isEnabled = false,
        )
        CustomTextField(
            value = guardia.firstOrNull()?.rh ?: stringResource(R.string.Dato_No_Obtenido),
            label = stringResource(R.string.Label_Rh_Profile),
            onValueChange = {},
            isEnabled = false,
        )

        // Usamos ButtonApp aquí también
        ButtonApp(text = stringResource(R.string.Text_profileScreen_Button), onClick =  { showDialog = true })
    }
    // Componente Modal
    if (showDialog) {
        Modal(showDialog, { showDialog = false },R.string.Name_Modal_Download,R.string.Content_Modal_Download, onClick = { showDialog = false })
    }
}