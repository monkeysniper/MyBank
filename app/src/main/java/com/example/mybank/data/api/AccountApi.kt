package com.example.mybank.data.api

import com.example.mybank.data.model.Account
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountApi {
    @GET("accounts")
    fun getAccounts(): Call<List<com.example.mybank.data.model.Account>>

    @POST("accounts")
    fun createAccount(@Body model: com.example.mybank.data.model.Account):Call<Account>
}