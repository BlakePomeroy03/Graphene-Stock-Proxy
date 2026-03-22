package com.blakepomeroy.graphenestockwidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StockWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val stockDataKey = stringPreferencesKey("stock_data")
        val lastUpdatedKey = stringPreferencesKey("last_updated")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val jsonString = prefs[stockDataKey]
            val lastUpdated = prefs[lastUpdatedKey] ?: "Never"

            // 1. Isolate the parsing logic OUTSIDE the UI nodes
            var liveData: List<StockData>? = null
            var parseError: String? = null

            if (jsonString != null && jsonString != "[]") {
                try {
                    val type = object : TypeToken<List<StockData>>() {}.type
                    liveData = Gson().fromJson(jsonString, type)
                } catch (e: Exception) {
                    parseError = e.message
                }
            }

            // 2. Safely render the UI based on the variables
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                if (jsonString == null) {
                    Text(
                        text = "Awaiting Worker...",
                        style = TextStyle(color = ColorProvider(Color.Yellow), fontSize = 16.sp)
                    )
                } else if (jsonString == "[]") {
                    Text(
                        text = "Python returned empty array.",
                        style = TextStyle(color = ColorProvider(Color.Yellow), fontSize = 16.sp)
                    )
                } else if (parseError != null) {
                    Text(
                        text = "JSON Crash: $parseError",
                        style = TextStyle(color = ColorProvider(Color.Red), fontSize = 12.sp)
                    )
                } else if (liveData != null) {
                    liveData.forEach { stock ->
                        StockRow(stock = stock)
                        Spacer(modifier = GlanceModifier.height(12.dp))
                    }

                    Spacer(modifier = GlanceModifier.defaultWeight())

                    Text(
                        text = "Last updated: $lastUpdated",
                        style = TextStyle(color = ColorProvider(Color.Gray), fontSize = 12.sp),
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun StockRow(stock: StockData) {
    val textColor = if (stock.isUp) Color(0xFF4CAF50) else Color(0xFFF44336)
    val sign = if (stock.isUp) "+" else ""

    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stock.ticker,
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.defaultWeight()
        )

        Text(
            text = "${stock.price}",
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 18.sp
            ),
            modifier = GlanceModifier.padding(end = 12.dp)
        )

        Text(
            text = "$sign${stock.change}",
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}