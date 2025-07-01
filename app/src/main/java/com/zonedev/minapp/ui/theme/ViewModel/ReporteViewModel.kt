package com.zonedev.minapp.ui.theme.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.zonedev.minapp.ui.theme.Model.Reporte
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReporteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val reportesCollection = db.collection("Reportes")

    // Función para crear un reporte
    fun crearReporte(tipo: String, parametros: Map<String, Any>, guardiaId: String) {
        viewModelScope.launch {
            try {
                val reporte = Reporte(
                    tipo = tipo,
                    parametros = parametros,
                    guardiaId = guardiaId,
                    timestamp = System.currentTimeMillis()
                )
                val reporteId = UUID.randomUUID().toString()
                reportesCollection.document(reporteId).set(reporte.toMap()).await()
                println("Reporte creado con éxito")
            } catch (e: Exception) {
                println("Error al crear reporte: ${e.message}")
            }
        }
    }


    // Función para leer un reporte específico por ID
    suspend fun leerReporte(id: String): Reporte? {
        return try {
            val snapshot = reportesCollection.document(id).get().await()
            snapshot.toObject(Reporte::class.java)
        } catch (e: Exception) {
            println("Error al leer reporte: ${e.message}")
            null
        }
    }

    // Función para leer todos los reportes de un guardia en particular
    suspend fun leerReportesPorGuardia(guardiaId: String): List<Reporte> {
        return try {
            val snapshot = reportesCollection
                .whereEqualTo("guardiaId", guardiaId)
                .get().await()
            snapshot.toObjects(Reporte::class.java)
        } catch (e: Exception) {
            println("Error al leer reportes: ${e.message}")
            emptyList()
        }
    }

    // Función para actualizar un reporte
    fun actualizarReporte(id: String, nuevosDatos: Map<String, Any>) {
        viewModelScope.launch {
            try {
                reportesCollection.document(id).update(nuevosDatos).await()
                println("Reporte actualizado con éxito")
            } catch (e: Exception) {
                println("Error al actualizar reporte: ${e.message}")
            }
        }
    }

    // Función para eliminar un reporte por ID
    fun eliminarReporte(id: String) {
        viewModelScope.launch {
            try {
                reportesCollection.document(id).delete().await()
                println("Reporte eliminado con éxito")
            } catch (e: Exception) {
                println("Error al eliminar reporte: ${e.message}")
            }
        }
    }

    suspend fun leerReportePorID(reporteId: String): Reporte? {
        return try {
            val snapshot = reportesCollection.document(reporteId).get().await()
            snapshot.toObject(Reporte::class.java)
        } catch (e: Exception) {
            println("Error al leer reporte: ${e.message}")
            null
        }
    }


    suspend fun leerReportesPorGuardiaYTipo(guardiaId: String, tipo: String): List<Reporte> {
        return try {
            val snapshot = reportesCollection
                .whereEqualTo("guardiaId", guardiaId)
                .whereEqualTo("tipo", tipo)
                .get().await()
            snapshot.toObjects(Reporte::class.java)
        } catch (e: Exception) {
            println("Error al leer reportes: ${e.message}")
            emptyList()
        }
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
            var query = reportesCollection.whereEqualTo("guardiaId", guardiaId)

            if (tipo.isNotEmpty()) {
                query = query.whereEqualTo("tipo", tipo) // Filtrar por tipo
            }

            if (id.isNotEmpty()) {
                query = query.whereEqualTo("parametros.Id_placa", id) // Filtrar por ID
            }

            if (nombre.isNotEmpty()) {
                query = reportesCollection
                    .whereGreaterThanOrEqualTo("parametros.Name",nombre)
                    .whereLessThan("parametros.Name","${nombre}\uF8FF") // Filtrar por nombre (asegúrate de que el campo existe)
            }

            if (fechaInicio != null && fechaFin != null) {
                query = reportesCollection
                    .whereGreaterThanOrEqualTo("timestamp", fechaInicio)
                    .whereLessThanOrEqualTo("timestamp", fechaFin)
            }
            val snapshot = query.get().await()
            snapshot.toObjects(Reporte::class.java)
        } catch (e: Exception) {
            println("Error al buscar reportes: ${e.message}")
            emptyList()
        }
    }

}