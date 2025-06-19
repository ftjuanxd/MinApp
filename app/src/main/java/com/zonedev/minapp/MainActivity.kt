package com.zonedev.minapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zonedev.minapp.ui.theme.Components.BaseScreen
import com.zonedev.minapp.ui.theme.MinappTheme
import com.zonedev.minapp.ui.theme.Screen.LoginApp
import com.zonedev.minapp.ui.theme.Screen.MainScreen
import com.zonedev.minapp.ui.theme.ViewModel.GuardiaViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var guardiaViewModel: GuardiaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        guardiaViewModel = GuardiaViewModel()
        enableEdgeToEdge()
        setContent {
            MinappTheme {
                val navController = rememberNavController()
                var idguard by remember { mutableStateOf<String?>(null) }
                val coroutineScope = rememberCoroutineScope()

                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController) // Splash Screen
                    }
                    composable("login") {
                        LoginApp(navController, auth) { userId ->
                            idguard = userId // Guarda el userId en la variable
                            // Cargar el guardia desde el ViewModel de forma asincrónica
                            coroutineScope.launch {
                                guardiaViewModel.getGuardiaById(userId)
                                navController.navigate("profile")
                            }
                        }
                    }
                    composable("profile") {
                        idguard?.let {
                            // Pasa el ID o instancia de Guardia según la lógica de tu BaseScreen
                            BaseScreen(navController = navController, guardiaViewModel = guardiaViewModel, guardiaId = it)
                        }
                    }
                }
            }
        }
    }
}
