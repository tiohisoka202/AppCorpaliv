package com.example.appcorpaliv.network

import retrofit2.Response
import retrofit2.http.*

interface ContenidosApiService {
    @GET("api/contenidos")
    suspend fun obtenerContenidos(): Response<List<ServicioInclusionDto>>
}

interface VinculacionApiService {
    @GET("api/solicitudes")
    suspend fun listarSolicitudes(): Response<List<SolicitudColaboracionDto>>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudColaboracionDto): Response<SolicitudColaboracionDto>

    @PUT("api/solicitudes/{id}/estado")
    suspend fun actualizarEstado(
        @Path("id") id: Long,
        @Query("nuevoEstado") nuevoEstado: String
    ): Response<SolicitudColaboracionDto>

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Long): Response<Unit>
}