package com.example.appcorpaliv.network

data class ServicioInclusionDto(
    val id: Long? = null,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val publicado: Boolean = true
)

data class SolicitudColaboracionDto(
    val id: Long? = null,
    val nombreContacto: String,
    val rutOOrganizacion: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val tipoColaboracion: String,
    val mensaje: String? = null,
    val estado: String? = "RECIBIDA",
    val fechaCreacion: String? = null
)