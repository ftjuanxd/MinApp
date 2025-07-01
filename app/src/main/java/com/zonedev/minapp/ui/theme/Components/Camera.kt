package com.zonedev.minapp.ui.theme.Components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun Camera(
    imageUri: Uri,
    onImageCaptured: (Uri) -> Unit,
    label: String= "Evidencia"
) {
    val context = LocalContext.current
    val uriState = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && uriState.value != null) {
            onImageCaptured(uriState.value!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && uriState.value != null) {
            cameraLauncher.launch(uriState.value!!)
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextFieldCamera(label, {
            val file = context.createImageFile()
            val uriForFile = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            uriState.value = uriForFile

            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraLauncher.launch(uriForFile)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }, imageUri)
    }
}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
}
