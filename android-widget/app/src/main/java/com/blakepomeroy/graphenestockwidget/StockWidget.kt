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
import androidx.glance.state.PreferencesGlanceStateDefinition // <-- Add this import
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StockWidget : GlanceAppWidget() {

    // THIS IS THE MISSING LINK: Tell Glance to use Android Preferences for state
    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val stockDataKey = stringPreferencesKey("stock_data")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // Now currentState<Preferences>() will actually work
            val prefs = currentState<Preferences>()
            val jsonString = prefs[stockDataKey] ?: "[]"

            val type = object : TypeToken<List<StockData>>() {}.type
            val liveData: List<StockData> = Gson().fromJson(jsonString, type)

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                if (liveData.isEmpty()) {
                    Text(
                        text = "Loading data...",
                        style = TextStyle(color = ColorProvider(Color.Gray), fontSize = 16.sp)
                    )
                } else {
                    liveData.forEach { stock ->
                        StockRow(stock = stock)
                        Spacer(modifier = GlanceModifier.height(12.dp))
                    }
                }
            }
        }
    }
}

// ... StockRow composable remains exactly the same below this ...

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