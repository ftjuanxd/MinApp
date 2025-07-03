package com.zonedev.minapp.ui.theme.Components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.zonedev.minapp.R
import com.zonedev.minapp.ui.theme.background
import com.zonedev.minapp.ui.theme.color_component
import com.zonedev.minapp.ui.theme.primary
import com.zonedev.minapp.ui.theme.text
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
