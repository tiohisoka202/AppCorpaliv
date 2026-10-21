package com.example.appcorpaliv.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Catalogo : Screen("catalogo_screen")
    data object Formulario : Screen("formulario_screen")
    data object Resumen : Screen("resumen_screen")
}