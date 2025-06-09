package com.zonedev.minapp.ui.theme.Model

data class Reporte(
    val tipo: String = "", // Observación, Acceso a Personas, Vehicular, Elementos
    val parametros: Map<String, Any> = mapOf(), // Parámetros específicos del reporte
    val guardiaId: String = "", // ID del guardia que realizó el reporte
    val timestamp: Long = System.currentTimeMillis() // Para ordenar los reportes
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "tipo" to tipo,
            "parametros" to parametros,
            "guardiaId" to guardiaId,
            "timestamp" to timestamp
        )
    }
}