package com.example.appcorpaliv.model

data class BeneficiarioModel(
    val id: Int = 0,
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val tipoDiscapacidad: String = "",
    val imagenUri: String? = null
)