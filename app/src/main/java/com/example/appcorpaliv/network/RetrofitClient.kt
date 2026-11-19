package com.example.appcorpaliv.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_CONTENIDOS = "http://10.0.2.2:9091/"
    private const val BASE_URL_VINCULACION = "http://10.0.2.2:9092/"

    val contenidosApi: ContenidosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CONTENIDOS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContenidosApiService::class.java)
    }

    val vinculacionApi: VinculacionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_VINCULACION)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VinculacionApiService::class.java)
    }
}