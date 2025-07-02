package com.zonedev.minapp.ui.theme.Components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun MostrarReporte(reporte: Reporte, tipo: String) {
    // Estado para gestionar qué URL de imagen está actualmente en pantalla completa.
    // Si es nulo, ninguna imagen está en pantalla completa.
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    val ordenParametros = when (tipo) {
        "Observacion" -> listOf("Titulo", "Observacion", "Evidencias")
        "Personal" -> listOf("Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        "Vehicular" -> listOf("Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        "Elemento" -> listOf("Imgelement", "Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        else -> listOf()
    }

    Column {
        Text(text = formatearFecha(reporte.timestamp))

        if (reporte.parametros.isNotEmpty()) {
            ordenParametros.forEach { key ->
                reporte.parametros[key]?.let { value ->
                    // Comprueba si el parámetro es una de nuestras claves de imagen y si el valor es un String no vacío.
                    if ((key == "Imgelement" || key == "Evidencias") && value is String && value.isNotEmpty() && value != "Ninguna") {
                        val imageUri = Uri.parse(value)
                        Text(text = "$key:")
                        // Muestra una miniatura de la imagen en la que se puede hacer clic.
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Evidencia del reporte",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Establece la URL que se mostrará en pantalla completa.
                                    fullScreenImageUrl = value
                                },
                            contentScale = ContentScale.Crop
                        ) {
                            when (painter.state) {
                                is AsyncImagePainter.State.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                                is AsyncImagePainter.State.Error -> {
                                    // Muestra un marcador de posición o un mensaje de error si la imagen no se carga.
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
                                        Text("No se pudo cargar la imagen")
                                    }
                                }
                                else -> {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        }
                    } else {
                        // Para todos los demás parámetros, muéstralos como texto.
                        Text(text = "$key: $value")
                    }
                }
            }
        } else {
            Text(text = "No hay parámetros disponibles")
        }
    }

    // Si se establece una URL de imagen, muestra el diálogo de pantalla completa.
    fullScreenImageUrl?.let { imageUrl ->
        FullScreenImageViewer(
            imageUrl = imageUrl,
            onDismiss = {
                // Restablece el estado para ocultar el diálogo.
                fullScreenImageUrl = null
            }
        )
    }
}

@Composable
fun FullScreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Clave para que el diálogo ocupe toda la pantalla.
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() }, // Permite cerrar tocando en cualquier lugar
            contentAlignment = Alignment.Center
        ) {
            // La imagen a pantalla completa.
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(imageUrl))
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen en pantalla completa",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit // 'Fit' asegura que toda la imagen sea visible
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }

            // Un botón para cerrar en la esquina superior derecha.
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
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


// El resto del archivo permanece igual.
fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, d MMM yyyy - h:mm a", Locale.getDefault())
    return sdf.format(timestamp)
}

fun obtenerParametro(reporte: Reporte, clave: String): String {
    val parametro = reporte.parametros[clave]
    return parametro?.toString() ?: "Parámetro no encontrado"
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
        else -> emptyMap() // Manejo de casos de tipos desconocidos
    }
}
