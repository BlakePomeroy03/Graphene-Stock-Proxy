package com.blakepomeroy.graphenestockwidget

import com.google.gson.annotations.SerializedName
data class StockData(
    @SerializedName("ticker") val ticker: String,
    @SerializedName("price") val price: Double,
    @SerializedName("change") val change: Double,
    @SerializedName("isUp") val isUp: Boolean
)