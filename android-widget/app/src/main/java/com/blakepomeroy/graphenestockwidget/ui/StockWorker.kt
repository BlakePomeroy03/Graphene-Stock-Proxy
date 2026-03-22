package com.blakepomeroy.graphenestockwidget

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson

class StockWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Fetch data
            val apiService = StockApiService.create()
            val stocks = apiService.getStocks()
            val jsonString = Gson().toJson(stocks)

            // 2. Save directly to the live Preferences state
            val manager = GlanceAppWidgetManager(context)
            manager.getGlanceIds(StockWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    // NO COPIES. Mutate the live database directly.
                    prefs[StockWidget.stockDataKey] = jsonString
                }
            }

            // 3. Force UI refresh
            StockWidget().updateAll(context)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}