package com.zonedev.minapp.ui.theme.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.CircularProgressIndicator
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.Components.ButtonApp
import com.zonedev.minapp.ui.theme.Components.CustomTextField
import com.zonedev.minapp.ui.theme.Components.Separetor
import com.zonedev.minapp.ui.theme.ViewModel.GuardiaViewModel
import com.zonedev.minapp.ui.theme.primary

@Composable
fun ProfileScreen(viewModel: GuardiaViewModel = viewModel()) {
    Components_Profile_Screen(viewModel)
}

@Composable
fun Components_Profile_Screen(guardiaViewModel: GuardiaViewModel = viewModel()){

    var showDialog by remember { mutableStateOf(false) }
    val guardia by guardiaViewModel.listaGuardias.collectAsState()

    // Imagen de perfil usando Coil
    guardia.firstOrNull()?.image?.let { imageUrl ->
        if (imageUrl.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.Descripcion_profileScreen_Image),
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 24.dp)
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
                            contentDescription = "Error loading image",
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

    Separetor()


    CustomTextField(
        value = guardia.firstOrNull()?.name ?: "Data not available",
        label = "Name",
        onValueChange = {},
        isEnabled = false
    )
    CustomTextField(
        value = guardia.firstOrNull()?.phone ?: "Data not available",
        label = "Phone",
        onValueChange = {},
        isEnabled = false
    )
    CustomTextField(
        value = guardia.firstOrNull()?.id ?: "Data not available",
        label = "N° Id",
        onValueChange = {},
        isEnabled = false
    )
    CustomTextField(
        value = guardia.firstOrNull()?.genre ?: "Data not available",
        label = "Genre",
        onValueChange = {},
        isEnabled = false
    )
    CustomTextField(
        value = guardia.firstOrNull()?.rh ?: "Data not available",
        label = "Rh",
        onValueChange = {},
        isEnabled = false
    )

    // Usamos ButtonApp aquí también
    ButtonApp(text = stringResource(R.string.Text_profileScreen_Button)) { showDialog = true }

    // Componente Modal
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text(
                text = stringResource(R.string.Name_Modal_Download),
                color = primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) },
            text = { Text(text = stringResource(R.string.Content_Modal_Download)) },
            confirmButton = {
                // Usa el botón personalizado dentro del modal
                ButtonApp(
                    text = stringResource(R.string.Value_Button_Report),
                    onClick = {
                        showDialog = false // Cierra el modal cuando se hace clic en "Aceptar"
                    },
                    //modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}