package com.example.appcorpaliv.viewmodel

data class FormularioState(
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val tipoDiscapacidad: String = "",
    val imagenUri: String? = null,

    // Errores para las validaciones
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorEmail: String? = null,

    // Estado general
    val esValido: Boolean = false,
    val guardadoExitoso: Boolean = false
)