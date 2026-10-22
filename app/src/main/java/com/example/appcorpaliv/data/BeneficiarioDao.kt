package com.example.appcorpaliv.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BeneficiarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarBeneficiario(beneficiario: BeneficiarioEntity)

    @Query("SELECT * FROM beneficiarios ORDER BY id DESC")
    fun obtenerTodosLosBeneficiarios(): Flow<List<BeneficiarioEntity>>
}