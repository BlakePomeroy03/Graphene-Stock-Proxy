package com.blakepomeroy.graphenestockwidget

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
interface StockApiService {

    @GET("api/stocks")
    suspend fun getStocks(): List<StockData>

    companion object {
        private const val BASE_URL = "http://192.168.1.12:8000/"

        fun create(): StockApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StockApiService::class.java)
        }
    }
}