package com.zonedev.minapp.ui.theme.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.zonedev.minapp.ui.theme.Model.Reporte
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

class ReporteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val reportesCollection = db.collection("Reporte")
    private val storage = FirebaseStorage.getInstance()

    // Función para una sola imagen (llama a la versión múltiple)
    fun subirImagenYCrearReporte(
        uriLocal: Uri,
        tipo: String,
        parametros: Map<String, Any>,
        guardiaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        subirImagenesYCrearReporte(listOf(uriLocal), tipo, parametros, guardiaId, onSuccess, onFailure)
    }

    // Función para múltiples imágenes
    fun subirImagenesYCrearReporte(
        urisLocales: List<Uri>,
        tipo: String,
        parametros: Map<String, Any>,
        guardiaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val downloadUrls = urisLocales.map { uri ->
                    async { subirUnaImagen(uri, tipo) }
                }.awaitAll()

                Log.d("ViewModel", "Todas las imágenes subidas. URLs: $downloadUrls")

                val parametrosCompletos = parametros.toMutableMap().apply {
                    this["Evidencias"] = downloadUrls
                    if (tipo == "Elemento") {
                        this["Imgelement"] = downloadUrls.firstOrNull() ?: ""
                    }
                }

                crearReporteInterno(tipo, parametrosCompletos, guardiaId)
                Log.d("ViewModel", "Reporte creado con éxito en Firestore.")
                onSuccess()

            } catch (e: Exception) {
                Log.e("ViewModel", "Error en la subida de imágenes o creación de reporte", e)
                onFailure(e)
            }
        }
    }

    private suspend fun subirUnaImagen(uri: Uri, tipoReporte: String): String {
        val nombreCarpeta = when (tipoReporte) {
            "Observacion" -> "observaciones_reports"
            "Elemento" -> "elementos_reports"
            else -> "otros_reports"
        }
        val rutaImagenEnStorage = "${nombreCarpeta}/${UUID.randomUUID()}.jpg"
        val imagenRef = storage.getReference(rutaImagenEnStorage)

        Log.d("ViewModel", "Iniciando subida de una imagen a: $rutaImagenEnStorage")
        val uploadTask = imagenRef.putFile(uri).await()
        val downloadUrl = uploadTask.storage.downloadUrl.await()
        Log.d("ViewModel", "Imagen subida: $downloadUrl")
        return downloadUrl.toString()
    }

    fun crearReporte(
        tipo: String,
        parametros: Map<String, Any>,
        guardiaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                crearReporteInterno(tipo, parametros, guardiaId)
                onSuccess()
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al crear reporte en Firestore", e)
                onFailure(e)
            }
        }
    }

    private suspend fun crearReporteInterno(
        tipo: String,
        parametros: Map<String, Any>,
        guardiaId: String
    ) {
        Log.d("ViewModel", "Creando reporte en Firestore con parámetros: $parametros")
        val reporte = Reporte(
            tipo = tipo,
            parametros = parametros,
            guardiaId = guardiaId,
            timestamp = System.currentTimeMillis()
        )
        val reporteId = UUID.randomUUID().toString()
        reportesCollection.document(reporteId).set(reporte.toMap()).await()
    }

    suspend fun buscarReportes(
        guardiaId: String,
        id: String,
        nombre: String,
        tipo: String,
        fechaInicio: Timestamp?,
        fechaFin: Timestamp?
    ): List<Reporte> {
        return try {
            var query: Query = reportesCollection
                .whereEqualTo("guardiaId", guardiaId)
                .whereEqualTo("tipo", tipo)
            when (tipo) {
                "Observacion" -> {
                    if (nombre.isNotEmpty()) {
                        val nombrestate = nombre.lowercase().trim()
                        query = query.whereGreaterThanOrEqualTo("parametros.Titulo", nombrestate)
                            .whereLessThan("parametros.Titulo", "${nombrestate}\uF8FF")
                    }
                }
                "Personal", "Vehicular" ,"Elemento" -> {
                    if (id.isNotEmpty()) {
                        val idstate = id.lowercase().trim()
                        query = query.whereGreaterThanOrEqualTo("parametros.Id_placa", idstate)
                            .whereLessThan("parametros.Id_placa", "${idstate}\uF8FF")
                    }
                    if (nombre.isNotEmpty()) {
                        val nombrestate = nombre.lowercase().trim()
                        query = query.whereGreaterThanOrEqualTo("parametros.Nombre", nombrestate)
                            .whereLessThan("parametros.Nombre", "${nombrestate}\uF8FF")
                    }
                }
            }
            if (fechaInicio != null) {
                query = query.whereGreaterThanOrEqualTo("timestamp", fechaInicio.toDate().time)
            }
            if (fechaFin != null) {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = fechaFin.toDate()
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val fechaFinEndOfDay = calendar.timeInMillis
                query = query.whereLessThanOrEqualTo("timestamp", fechaFinEndOfDay)
            }
            val snapshot = query.get().await()
            snapshot.toObjects(Reporte::class.java)
        } catch (e: Exception) {
            println("Error al buscar reportes (posiblemente índice de Firestore faltante): ${e.message}")
            emptyList()
        }
    }
}