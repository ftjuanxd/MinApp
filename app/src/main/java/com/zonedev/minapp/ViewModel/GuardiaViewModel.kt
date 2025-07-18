package com.zonedev.minapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zonedev.minapp.Model.Guardia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GuardiaViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val pathGuardia = "Guardia"
    private val pathPuesto = "Puesto de Trabajo"

    private var _listaGuardia = MutableStateFlow<List<Guardia>>(emptyList())
    val listaGuardias = _listaGuardia.asStateFlow()

    private var _nombrePuesto = MutableStateFlow("Cargando puesto...")
    val nombrePuesto = _nombrePuesto.asStateFlow()

    fun getGuardiaById(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Obtener los datos del guardia
                val document = db.collection(pathGuardia).document(userId).get().await()
                val guardia = document.toObject(Guardia::class.java)

                if (guardia != null) {
                    _listaGuardia.value = listOf(guardia)
                    // 2. Si el guardia tiene un puestoId, buscar su nombre
                    if (guardia.puestoId.isNotEmpty()) {
                        fetchPuestoNombre(guardia.puestoId)
                    } else {
                        _nombrePuesto.value = "Sin puesto asignado"
                    }
                } else {
                    _listaGuardia.value = emptyList()
                    _nombrePuesto.value = "Guardia no encontrado"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _nombrePuesto.value = "Error al cargar datos"
            }
        }
    }

    private suspend fun fetchPuestoNombre(puestoId: String) {
        try {
            val puestoDocument = db.collection(pathPuesto).document(puestoId).get().await()
            // Suponiendo que el campo en Firestore se llama 'name'
            val nombre = puestoDocument.getString("name")
            _nombrePuesto.value = nombre ?: "Puesto no encontrado"
        } catch (e: Exception) {
            e.printStackTrace()
            _nombrePuesto.value = "Error al buscar puesto"
        }
    }
}