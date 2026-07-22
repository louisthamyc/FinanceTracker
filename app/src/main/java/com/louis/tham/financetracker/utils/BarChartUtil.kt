package com.louis.tham.financetracker.utils

import android.graphics.Paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Locale

object BarChartUtil {

    /**
     * Renders a bar chart inside the provided [DrawScope] using Canvas.
     * Groups transaction entities by month, sums their amounts, and renders them.
     *
     * @param drawScope The DrawScope from Compose Canvas.
     * @param transactions List of transactions to visualize.
     * @param barColorStart Gradient start color for the bars.
     * @param barColorEnd Gradient end color for the bars.
     * @param axisColor Color used for drawing the X and Y axes.
     * @param textColor Color used for text labels.
     * @param gridLineColor Color used for the horizontal gridlines.
     */
    fun drawBarChart(
        drawScope: DrawScope,
        transactions: List<TransactionEntity>,
        barColorStart: Color = Color(0xFF6650A4),
        barColorEnd: Color = Color(0xFFD0BCFF),
        axisColor: Color = Color(0xFFCCCCCC),
        textColor: Color = Color(0xFF666666),
        gridLineColor: Color = Color(0xFFE5E5E5)
    ) {
        val width = drawScope.size.width
        val height = drawScope.size.height

        if (transactions.isEmpty()) {
            val paintText = Paint().apply {
                color = textColor.toArgb()
                textSize = 40f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                "No transactions recorded yet",
                width / 2f,
                height / 2f,
                paintText
            )
            return
        }

        // Group transactions by month, sum amounts, and sort chronologically.
        // Month format: "yyyy-MM"
        val groupedData = transactions
            .groupBy { transaction ->
                if (transaction.date.length >= 7) {
                    transaction.date.substring(0, 7)
                } else {
                    transaction.date
                }
            }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedBy { it.first } // Sorted chronologically by year-month
            .takeLast(6)          // Limit to latest 6 months with transactions for layout aesthetic

        if (groupedData.isEmpty()) {
            return
        }

        val maxAmount = groupedData.maxOf { it.second }
        // Prevent division by zero
        val maxVal = if (maxAmount <= 0.0) 10.0 else maxAmount

        // Define paddings
        val paddingLeft = 140f
        val paddingRight = 40f
        val paddingTop = 60f
        val paddingBottom = 80f

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        // Draw Y-axis grid lines and labels
        val paintText = Paint().apply {
            color = textColor.toArgb()
            textSize = 28f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        val gridLinesCount = 3
        for (i in 0..gridLinesCount) {
            val ratio = i.toFloat() / gridLinesCount
            val y = paddingTop + chartHeight * (1 - ratio)
            val value = maxVal * ratio

            // Draw horizontal grid line
            drawScope.drawLine(
                color = if (i == 0) axisColor else gridLineColor,
                start = Offset(paddingLeft, y),
                end = Offset(width - paddingRight, y),
                strokeWidth = if (i == 0) 4f else 2f
            )

            // Format label e.g., "500" or "1.5k"
            val label = when {
                value >= 1000.0 -> String.format(Locale.US, "%.1fk", value / 1000.0)
                else -> String.format(Locale.US, "%.0f", value)
            }
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                label,
                paddingLeft - 20f,
                y + 10f,
                paintText
            )
        }

        // Draw Bars and X-axis Labels
        val barCount = groupedData.size
        val spacingRatio = 0.35f
        val totalSpacingWidth = chartWidth * spacingRatio
        val totalBarsWidth = chartWidth * (1 - spacingRatio)
        val barWidth = totalBarsWidth / barCount
        val barSpacing = totalSpacingWidth / (barCount + 1)

        val paintXLabel = Paint().apply {
            color = textColor.toArgb()
            textSize = 26f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val paintBarVal = Paint().apply {
            color = Color.White.toArgb()
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val monthInputFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val dayInputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM", Locale.getDefault())

        for (index in 0 until barCount) {
            val (dateStr, amount) = groupedData[index]

            // Calculate x & y positions
            val xStart = paddingLeft + barSpacing + index * (barWidth + barSpacing)
            val barHeightVal = (amount / maxVal * chartHeight).toFloat()
            val yStart = paddingTop + chartHeight - barHeightVal

            // Format month string for displaying underneath
            val displayDate = try {
                val date = if (dateStr.length == 7) {
                    monthInputFormat.parse(dateStr)
                } else {
                    dayInputFormat.parse(dateStr)
                }
                if (date != null) outputFormat.format(date) else dateStr
            } catch (_: Exception) {
                dateStr
            }

            // Define Brush Gradient
            val brush = Brush.verticalGradient(
                colors = listOf(barColorStart, barColorEnd),
                startY = yStart,
                endY = paddingTop + chartHeight
            )

            if (barHeightVal > 0f) {
                // Draw rounded rect bar
                drawScope.drawRoundRect(
                    brush = brush,
                    topLeft = Offset(xStart, yStart),
                    size = Size(barWidth, barHeightVal),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Flatten the bottom of the rounded rectangle
                if (barHeightVal > 15f) {
                    drawScope.drawRect(
                        brush = brush,
                        topLeft = Offset(xStart, yStart + barHeightVal - 15f),
                        size = Size(barWidth, 15f)
                    )
                }

                // If bar is tall enough, draw the amount value inside the bar (near the top)
                if (barHeightVal > 50f) {
                    val displayAmt = when {
                        amount >= 1000.0 -> String.format(Locale.US, "%.1fk", amount / 1000.0)
                        else -> String.format(Locale.US, "%.0f", amount)
                    }
                    drawScope.drawContext.canvas.nativeCanvas.drawText(
                        displayAmt,
                        xStart + barWidth / 2f,
                        yStart + 35f,
                        paintBarVal
                    )
                }
            }

            // Draw X-axis label (month)
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                displayDate,
                xStart + barWidth / 2f,
                paddingTop + chartHeight + 45f,
                paintXLabel
            )
        }
    }
}
