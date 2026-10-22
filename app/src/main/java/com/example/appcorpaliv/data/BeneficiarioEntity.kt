package com.example.appcorpaliv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beneficiarios")
data class BeneficiarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val rut: String,
    val email: String,
    val telefono: String,
    val tipoDiscapacidad: String,
    val imagenUri: String?
)