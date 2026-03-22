package com.blakepomeroy.graphenestockwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workManager = WorkManager.getInstance(this)

        val immediateRequest = OneTimeWorkRequestBuilder<StockWorker>().build()
        workManager.enqueue(immediateRequest)

        val periodicRequest = PeriodicWorkRequestBuilder<StockWorker>(15, TimeUnit.MINUTES).build()
        workManager.enqueueUniquePeriodicWork(
            "StockWidgetPeriodicUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        setContent {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Widget engine is running. You can close this app.")
            }
        }
    }
}