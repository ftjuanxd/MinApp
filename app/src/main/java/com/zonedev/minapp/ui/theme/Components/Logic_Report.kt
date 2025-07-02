package com.zonedev.minapp.ui.theme.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        else -> emptyMap() // Manejo de casos de tipos desconocidos
    }
}

@Composable
fun MostrarReporte(reporte: Reporte,tipo: String) {
    val ordenParametros = when(tipo){
        "Observacion" -> listOf("Titulo", "Observacion", "Evidencias")
        "Personal" -> listOf("Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        "Vehicular" -> listOf("Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        "Elemento" -> listOf("Imgelement", "Id_placa", "Nombre", "Destino", "Autorizacion", "Descripcion")
        else -> listOf()
    }

    Column {
        Text(text = "${formatearFecha(reporte.timestamp)}")

        if (reporte.parametros.isNotEmpty()) {
            // Itera sobre la lista de claves en orden
            ordenParametros.forEach { key ->
                reporte.parametros[key]?.let { value ->
                    Text(text = "$key: $value") // Muestra cada clave y valor en orden específico
                    //println("Mostrar data \n$key: $value")
                }
            }
        } else {
            Text(text = "No hay parámetros disponibles")
        }
    }
}


fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, d MMM yyyy - h:mm a", Locale.getDefault())
    return sdf.format(timestamp)
}
fun obtenerParametro(reporte: Reporte, clave: String): String {
    val parametro = reporte.parametros[clave]

    // Agrega un log para verificar qué parámetros están presentes en el reporte
    //println("Parametros: ${reporte.parametros}")

    return parametro?.toString() ?: "Parámetro no encontrado"
}

fun obtenerClavePorTipo(tipo: String): String {
    //println("TIPO De dato $tipo")
    return when (tipo) {
        "Observacion" -> "Titulo"
        "Personal" -> "Id_placa"
        "Vehicular" -> "Id_placa"
        "Elemento" -> "Id_placa"
        else -> "unknown"
    }
}
