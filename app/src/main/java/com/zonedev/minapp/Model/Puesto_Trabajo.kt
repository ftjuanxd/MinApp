package com.zonedev.minapp.Model

data class Puesto_Trabajo (
    val name : String = "",
    val Ciudad : String = "",
    val Direccion : String = "",
){
    fun toMap() : Map<String, String> {
        return mapOf(
            "name" to name,
            "Ciudad" to Ciudad,
            "Direccion" to Direccion
        )
    }
}