package com.zonedev.minapp.ui.Components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
private fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    // Se usa el caché interno como alternativa segura si el externo no está disponible.
    val storageDir = externalCacheDir ?: cacheDir
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}


@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    selectedUris: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    allowMultiple: Boolean,
    label: String = stringResource(R.string.Label_Upload_Files)
) {
    if (allowMultiple) {
        MultiImagePicker(modifier, selectedUris, onImagesSelected, label)
    } else {
        SingleImagePicker(modifier, selectedUris.firstOrNull(), onImagesSelected, label)
    }
}
@Composable
private fun SingleImagePicker(
    modifier: Modifier = Modifier,
    selectedUri: Uri?,
    onImagesSelected: (List<Uri>) -> Unit,
    label: String
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para obtener la imagen de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempUri?.let { uri -> onImagesSelected(listOf(uri)) }
            }
        }
    )

    // --- ¡NUEVO! Launcher para solicitar el permiso de la cámara ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Si el permiso es concedido, lanza la cámara
                val file = context.createImageFile()
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                tempUri = uri
                cameraLauncher.launch(uri)
            } else {
                // Opcional: Mostrar un mensaje al usuario si deniega el permiso
            }
        }
    )

    val launchCamera: () -> Unit = {
        val permission = Manifest.permission.CAMERA
        val permissionStatus = ContextCompat.checkSelfPermission(context, permission)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // Si ya tenemos permiso, lanza la cámara directamente
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            // Si no tenemos permiso, lo solicita
            permissionLauncher.launch(permission)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Text(
            text = label,
            color = primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, primary, RoundedCornerShape(12.dp))
                .clickable { launchCamera() }, // Llama a nuestra nueva función
            contentAlignment = Alignment.Center
        ) {
            if (selectedUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedUri),
                    contentDescription = "Imagen seleccionada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { onImagesSelected(emptyList()) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.White)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Tomar foto",
                        tint = color_component,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(text = "Tomar Foto", color = color_component)
                }
            }
        }
    }
}


@Composable
private fun MultiImagePicker(
    modifier: Modifier = Modifier,
    selectedUris: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    label: String
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para la galería
    val multipleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            val currentUris = selectedUris.toMutableList()
            currentUris.addAll(uris)
            onImagesSelected(currentUris.distinct())
        }
    )

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempUri?.let { uri ->
                    val currentUris = selectedUris.toMutableList()
                    currentUris.add(uri)
                    onImagesSelected(currentUris)
                }
            }
        }
    )

    // --- Launcher para solicitar el permiso de la cámara ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val file = context.createImageFile()
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                tempUri = uri
                cameraLauncher.launch(uri)
            }
        }
    )

    val launchCamera: () -> Unit = {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = primary,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 110.dp) // Ajuste de altura para mejor visualización
                .border(2.dp, primary, RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedUris) { uri ->
                ImagePreviewItem(uri = uri) {
                    val updatedUris = selectedUris.toMutableList().apply { remove(uri) }
                    onImagesSelected(updatedUris)
                }
            }
            item {
                AddImageButton(
                    icon = Icons.Default.PhotoLibrary,
                    text = "Galería",
                    onClick = { multipleImagePickerLauncher.launch("image/*") }
                )
            }
            item {
                AddImageButton(
                    icon = Icons.Default.PhotoCamera,
                    text = "Cámara",
                    onClick = launchCamera // Llama a nuestra nueva función
                )
            }
        }
    }
}


@Composable
fun ImagePreviewItem(uri: Uri, onRemoveClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar imagen",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AddImageButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, color_component, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = text, tint = color_component, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, color = color_component, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}