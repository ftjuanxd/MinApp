package com.zonedev.minapp.ui.theme.ViewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocationViewModel : ViewModel(){

    private val db = Firebase.firestore

    private val path = "Puesto_Trabajo"

}