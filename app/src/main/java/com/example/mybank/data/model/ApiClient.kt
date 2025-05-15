package com.example.mybank.data.model

import com.example.mybank.data.api.AccountApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val loggingInterceptor=HttpLoggingInterceptor().apply {
        level=HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient=OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit:Retrofit=Retrofit.Builder()
        .baseUrl("https://681e0ef2c1c291fa6632dc4f.mockapi.io/api/v1/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val accountApi: AccountApi = retrofit.create(AccountApi::class.java)
}