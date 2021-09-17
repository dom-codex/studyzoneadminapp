package com.sparktech.studyzoneadmin.network

import com.sparktech.studyzoneadmin.helpers.Network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {
    private const val BASE_URl = Network.BASE_URL
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    val apiService = getRetrofit().create(ApiService::class.java)
}