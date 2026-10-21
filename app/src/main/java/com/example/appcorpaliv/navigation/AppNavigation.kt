package com.example.appcorpaliv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcorpaliv.ui.screens.CatalogoScreen
import com.example.appcorpaliv.ui.screens.FormularioScreen
import com.example.appcorpaliv.ui.screens.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Formulario.route) {
            FormularioScreen(navController = navController)
        }
        composable(Screen.Catalogo.route) {
            CatalogoScreen(navController = navController)
        }
    }
}