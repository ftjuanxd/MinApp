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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

class ReporteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val reportesCollection = db.collection("Reporte")
    private val storage = FirebaseStorage.getInstance()

    fun subirImagenYCrearReporte(
        uriLocal: Uri,
        tipo: String,
        parametros: Map<String, Any>,
        guardiaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val rutaImagenEnStorage = "imagenes_reportes/${UUID.randomUUID()}.jpg"
        val imagenRef = storage.getReference(rutaImagenEnStorage)

        Log.d("ViewModel", "Iniciando subida a: $rutaImagenEnStorage")

        imagenRef.putFile(uriLocal)
            .addOnSuccessListener { uploadTask ->
                Log.d("ViewModel", "Subida de imagen exitosa. Obteniendo URL de descarga...")
                uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                    Log.d("ViewModel", "URL de descarga obtenida: $downloadUrl")
                    val parametrosCompletos = parametros.toMutableMap()
                    parametrosCompletos["Imgelement"] = downloadUrl.toString()
                    parametrosCompletos["Evidencias"] = downloadUrl.toString() // Para consistencia con Observaciones

                    crearReporte(tipo, parametrosCompletos, guardiaId, onSuccess, onFailure)

                }.addOnFailureListener { exception ->
                    Log.e("ViewModel", "Error al obtener URL de descarga", exception)
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ViewModel", "Error en la subida de imagen", exception)
                onFailure(exception)
            }
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
                Log.d("ViewModel", "Creando reporte en Firestore con parámetros: $parametros")
                val reporte = Reporte(
                    tipo = tipo,
                    parametros = parametros,
                    guardiaId = guardiaId,
                    timestamp = System.currentTimeMillis()
                )
                val reporteId = UUID.randomUUID().toString()
                reportesCollection.document(reporteId).set(reporte.toMap()).await()
                Log.d("ViewModel", "Reporte creado con éxito en Firestore.")
                onSuccess()
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al crear reporte en Firestore", e)
                onFailure(e)
            }
        }
    }
    // La función buscarReportes no necesita cambios.
    suspend fun buscarReportes(
        guardiaId: String,
        id: String,
        nombre: String,
        tipo: String,
        fechaInicio: Timestamp?,
        fechaFin: Timestamp?
    ): List<Reporte> {
        return try {
            // La consulta base siempre filtra por guardia y por el tipo de reporte seleccionado.
            var query: Query = reportesCollection
                .whereEqualTo("guardiaId", guardiaId)
                .whereEqualTo("tipo", tipo)

            // Aplica los filtros de ID y Nombre de forma condicional según el tipo de reporte.
            when (tipo) {
                "Observacion" -> {
                    // Para "Observacion", el filtro de "nombre" busca en el campo "Titulo".
                    if (nombre.isNotEmpty()) {
                        val nombrestate = nombre.lowercase().trim()
                        query = query.whereGreaterThanOrEqualTo("parametros.Titulo", nombrestate)
                            .whereLessThan("parametros.Titulo", "${nombrestate}\uF8FF")
                    }
                }
                "Personal", "Vehicular", "Elemento" -> {
                    // Para los otros tipos, el filtro de "ID" busca en "Id_placa".
                    if (id.isNotEmpty()) {
                        query = query.whereEqualTo("parametros.Id_placa", id)
                    }
                    // Y el filtro de "nombre" busca en "Nombre".
                    if (nombre.isNotEmpty()) {
                        val nombrestate = nombre.lowercase().trim()
                        query = query.whereGreaterThanOrEqualTo("parametros.Nombre", nombrestate)
                            .whereLessThan("parametros.Nombre", "${nombrestate}\uF8FF")
                    }
                }
            }

            // El campo 'timestamp' en el modelo de datos es un Long.
            // Por lo tanto, la consulta debe usar el valor en milisegundos (Long).
            if (fechaInicio != null) {
                query = query.whereGreaterThanOrEqualTo("timestamp", fechaInicio.toDate().time)
            }

            if (fechaFin != null) {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = fechaFin.toDate()
                // Ajusta la fecha de fin para que incluya todo el día hasta las 23:59:59.
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