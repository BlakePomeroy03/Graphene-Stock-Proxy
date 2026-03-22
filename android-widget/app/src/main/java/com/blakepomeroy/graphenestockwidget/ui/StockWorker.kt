package com.blakepomeroy.graphenestockwidget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class StockWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val apiService = StockApiService.create()
            val stocks = apiService.getStocks()
            val jsonString = Gson().toJson(stocks)

            // Capture the exact time the fetch succeeded
            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            val currentTime = LocalTime.now().format(formatter)

            val manager = GlanceAppWidgetManager(context)
            manager.getGlanceIds(StockWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[StockWidget.stockDataKey] = jsonString
                    // Save the timestamp to the database
                    prefs[StockWidget.lastUpdatedKey] = currentTime
                }
            }

            StockWidget().updateAll(context)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}