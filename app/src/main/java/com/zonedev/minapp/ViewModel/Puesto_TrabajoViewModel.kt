package com.zonedev.minapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Puesto_TrabajoViewModel : ViewModel(){
// La clase ya funciona solo debe ser llamada y trabajada desde el view model y el dato guardado debe estar asi:
    //val nombres: List<String> by puestoViewModel.listaNombresPuestos.collectAsState() y se muestra dentro de un lazycolumn o parecido
    private val db = Firebase.firestore

    private val path = "Puesto de Trabajo"

    private val _listaNombresPuestos = MutableStateFlow<List<String>>(emptyList())
    val listaNombresPuestos = _listaNombresPuestos.asStateFlow()

    init {
        fetchallPuestoName()
    }
    fun fetchallPuestoName() {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val snapshot = db.collection(path).get().await()
                val puesto = snapshot.documents.mapNotNull { document ->
                    document.getString("name")
                }
                _listaNombresPuestos.value=puesto
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}