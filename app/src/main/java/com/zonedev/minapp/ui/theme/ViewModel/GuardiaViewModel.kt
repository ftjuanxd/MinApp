package com.zonedev.minapp.ui.theme.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zonedev.minapp.ui.theme.Model.Guardia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GuardiaViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val path = "Guardia"

    private var _listaGuardia = MutableStateFlow<List<Guardia>>(emptyList())
    val listaGuardias = _listaGuardia.asStateFlow()

    fun getGuardiaById(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = db.collection(path).document(userId).get().await()
                val guardia = document.toObject(Guardia::class.java)
                guardia?.let {
                    _listaGuardia.value = listOf(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}