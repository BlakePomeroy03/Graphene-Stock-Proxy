package com.blakepomeroy.graphenestockwidget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.runtime.Composable


class StockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val mockData = listOf(
            StockData("XLK", 151.45, 4.50, true),
            StockData("AMD", 202.02, -2.56, false),
            StockData("VOO", 480.10, 1.20, true)
        )

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                mockData.forEach { stock ->
                    StockRow(stock = stock)
                    Spacer(modifier = GlanceModifier.height(12.dp))
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