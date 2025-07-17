package com.zonedev.minapp.Model

data class Reporte(
    val tipo: String = "", // Observación, Acceso a Personas, Vehicular, Elementos
    val parametros: Map<String, Any> = mapOf(), // Parámetros específicos del reporte
    val guardiaId: String = "", // ID del guardia que realizó el reporte
    val timestamp: Long = System.currentTimeMillis(), // Para ordenar los reportes
    val guardiaNombre: String = "", // Nombre del guardia - este se mostrara en pantalla
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "tipo" to tipo,
            "parametros" to parametros,
            "guardiaId" to guardiaId,
            "timestamp" to timestamp
            // Atencion: guardiaNombre no se incluye aquí porque no es un campo en Firestore.
        )
    }
}