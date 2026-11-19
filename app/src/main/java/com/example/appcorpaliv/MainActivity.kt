package com.example.appcorpaliv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcorpaliv.navigation.Screen
import com.example.appcorpaliv.ui.screens.CatalogoScreen
import com.example.appcorpaliv.ui.screens.ContactoEmpresaScreen
import com.example.appcorpaliv.ui.screens.DonacionesScreen
import com.example.appcorpaliv.ui.screens.FormularioScreen
import com.example.appcorpaliv.ui.screens.HomeScreen
import com.example.appcorpaliv.ui.theme.AppCorpalivTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCorpalivTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Pantalla de Inicio
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Formulario 1: Beneficiarios
        composable(Screen.Formulario.route) {
            FormularioScreen(navController = navController)
        }

        // Formulario 2: Contacto Empresas
        composable(Screen.ContactoEmpresa.route) {
            ContactoEmpresaScreen(
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Donaciones
        composable(Screen.Donaciones.route) {
            DonacionesScreen(
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // Catálogo de Iniciativas y Servicios
        composable(Screen.Catalogo.route) {
            CatalogoScreen(navController = navController)
        }
    }
}