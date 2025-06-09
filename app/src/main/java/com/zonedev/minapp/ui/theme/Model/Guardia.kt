package com.zonedev.minapp.ui.theme.Model

data class Guardia(
    val image: String = "",
    val name: String = "",
    val phone: String = "",
    var id: String = "",
    val genre: String = "",
    val rh: String = "",
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "image" to image,
            "name" to name,
            "phone" to phone,
            "genre" to genre,
            "rh" to rh
        )
    }
}
