package com.zonedev.minapp.ui.theme.Components.Report

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.zonedev.minapp.ui.theme.Model.Reporte
import java.text.SimpleDateFormat
import java.util.Locale

fun crearParametrosParaReporte(tipo: String, datos: Map<String, Any?>): Map<String, Any> {
    println("Creando parámetros para tipo: $tipo con datos: $datos")
    return when (tipo) {
        "Observacion" -> {
            mapOf(
                "Titulo" to (datos["Titulo"] ?: ""),
                "Observacion" to (datos["Observacion"] ?: ""),
                "Evidencias" to (datos["Evidencias"] ?: "Ninguna"),
            )
        }
        "Personal" -> {
            mapOf(
                "Id_placa" to (datos["Id_placa"] ?: ""),
                "Nombre" to (datos["Nombre"] ?:""),
                "Destino" to (datos["Destino"] ?: ""),
                "Autorizacion" to (datos["Autorizacion"]?: ""),
                "Descripcion" to (datos["Descripcion"]?: "")
            )
        }
        "Vehicular" -> {
            mapOf(
                "Id_placa" to (datos["Id_placa"] ?: ""),
                "Nombre" to (datos["Nombre"] ?:""),
                "Destino" to (datos["Destino"] ?: ""),
                "Autorizacion" to (datos["Autorizacion"]?: ""),
                "Descripcion" to (datos["Descripcion"]?: "")
            )
        }
        "Elemento" -> {
            mapOf(
                "Imgelement" to (datos["Imgelement"] ?: ""),
                "Id_placa" to (datos["Id_placa"] ?: ""),
                "Nombre" to (datos["Nombre"] ?:""),
                "Destino" to (datos["Destino"] ?: ""),
                "Autorizacion" to (datos["Autorizacion"]?: ""),
                "Descripcion" to (datos["Descripcion"]?: "")
            )
        }
        else -> emptyMap()
    }
}

fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, d MMM, yyyy - h:mm a", Locale.getDefault())
    return sdf.format(timestamp)
}

@Composable
fun FullScreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(imageUrl))
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen en pantalla completa",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    else -> SubcomposeAsyncImageContent()
                }
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end=12.dp,top=26.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


@Composable
fun MostrarReporte(reporte: Reporte, tipo: String) {
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    val ordenParametros = when (tipo) {
        "Observacion" -> listOf("Titulo", "Observacion", "Evidencias")
        "Personal", "Vehicular" -> listOf("Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        "Elemento" -> listOf("Imgelement", "Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        else -> listOf()
    }

    Column {
        Text(text = formatearFecha(reporte.timestamp))

        // Recopilar todas las URLs de imágenes primero
        val allImageUrls = mutableListOf<String>()
        reporte.parametros["Imgelement"]?.let { value ->
            if (value is String && value.isNotEmpty()) {
                allImageUrls.add(value)
            }
        }
        reporte.parametros["Evidencias"]?.let { value ->
            when (value) {
                is String -> if (value.isNotEmpty() && value != "Ninguna") allImageUrls.add(value) else {}
                is List<*> -> allImageUrls.addAll(value.filterIsInstance<String>())
                else -> {}
            }
        }

        // Mostrar los parámetros de texto
        ordenParametros.forEach { key ->
            if (key != "Imgelement" && key != "Evidencias") {
                reporte.parametros[key]?.let { value ->
                    Text(text = "$key: $value")
                }
            }
        }

        // Mostrar el carrusel de imágenes si hay alguna
        if (allImageUrls.isNotEmpty()) {
            Text(text = if (tipo == "Elemento") "Imagen del Elemento:" else "Evidencias:")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(allImageUrls) { imageUrl ->
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            .clickable { fullScreenImageUrl = imageUrl}
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                            contentDescription = "Evidencia",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        ) {
                            when (painter.state) {
                                is AsyncImagePainter.State.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                is AsyncImagePainter.State.Error -> Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) { Text("Error") }
                                else -> SubcomposeAsyncImageContent()
                            }
                        }
                    }
                }
            }
        }

    }

    // Lógica para el visor de pantalla completa
    fullScreenImageUrl?.let { imageUrl ->
        FullScreenImageViewer(imageUrl = imageUrl) {
            fullScreenImageUrl = null
        }
    }
}

fun obtenerClavePorTipo(tipo: String): String {
    return when (tipo) {
        "Observacion" -> "Titulo"
        "Personal" -> "Id_placa"
        "Vehicular" -> "Id_placa"
        "Elemento" -> "Id_placa"
        else -> "unknown"
    }
}


fun obtenerParametro(reporte: Reporte, clave: String): String {
    val parametro = reporte.parametros[clave]
    return parametro?.toString() ?: "Parámetro no encontrado"
}