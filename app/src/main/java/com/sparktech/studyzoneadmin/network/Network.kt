package com.sparktech.studyzoneadmin.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {
    private const val BASE_URl = "http://10.0.2.2:4500"
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    val apiService = getRetrofit().create(ApiService::class.java)
}