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
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

object BarChartUtil {

    private fun getSignedAmount(transaction: TransactionEntity): Double {
        val typeName = transaction.type
        return when {
            typeName.equals(TransactionType.INCOME.name, ignoreCase = true) ||
            typeName.equals("INCOME", ignoreCase = true) -> abs(transaction.amount)

            typeName.equals(TransactionType.EXPENSE.name, ignoreCase = true) ||
            typeName.equals("EXPENSE", ignoreCase = true) -> -abs(transaction.amount)

            transaction.amount < 0 -> transaction.amount
            else -> -transaction.amount
        }
    }

    private fun formatAmountLabel(amt: Double): String {
        val absAmt = abs(amt)
        val formattedStr = when {
            absAmt >= 1000.0 -> String.format(Locale.US, "%.1fk", absAmt / 1000.0)
            else -> String.format(Locale.US, "%.0f", absAmt)
        }
        return if (amt > 0) "+$formattedStr" else if (amt < 0) "-$formattedStr" else "0"
    }

    private fun formatYLabel(value: Double): String {
        val absVal = abs(value)
        if (absVal < 0.001) return "0"
        val formatted = when {
            absVal >= 1000.0 -> String.format(Locale.US, "%.1fk", absVal / 1000.0)
            else -> String.format(Locale.US, "%.0f", absVal)
        }
        return if (value > 0) "+$formatted" else "-$formatted"
    }

    /**
     * Renders a bar chart inside the provided [DrawScope] using Canvas.
     * Groups transaction entities by month, calculates net amounts (+ for income, - for expenses),
     * and renders positive and negative bars relative to a zero baseline.
     *
     * @param drawScope The DrawScope from Compose Canvas.
     * @param transactions List of transactions to visualize.
     * @param positiveBarColorStart Gradient start color for positive bars (Income).
     * @param positiveBarColorEnd Gradient end color for positive bars (Income).
     * @param negativeBarColorStart Gradient start color for negative bars (Expense).
     * @param negativeBarColorEnd Gradient end color for negative bars (Expense).
     * @param axisColor Color used for drawing the X, Y, and zero axes.
     * @param textColor Color used for text labels.
     * @param gridLineColor Color used for horizontal gridlines.
     */
    fun drawBarChart(
        drawScope: DrawScope,
        transactions: List<TransactionEntity>,
        positiveBarColorStart: Color = Color(0xFF16A34A),
        positiveBarColorEnd: Color = Color(0xFF4ADE80),
        negativeBarColorStart: Color = Color(0xFFE11D48),
        negativeBarColorEnd: Color = Color(0xFFFB7185),
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

        // Group transactions by month, calculate net amount (income vs expenses), and sort chronologically.
        // Month format: "yyyy-MM"
        val groupedData = transactions
            .groupBy { transaction ->
                if (transaction.date.length >= 7) {
                    transaction.date.substring(0, 7)
                } else {
                    transaction.date
                }
            }
            .mapValues { entry -> entry.value.sumOf { getSignedAmount(it) } }
            .toList()
            .sortedBy { it.first } // Sorted chronologically by year-month
            .takeLast(3)          // Limit to latest 3 months with transactions

        if (groupedData.isEmpty()) {
            return
        }

        val values = groupedData.map { it.second }
        val maxDataVal = values.maxOrNull() ?: 0.0
        val minDataVal = values.minOrNull() ?: 0.0

        // Scale bounds with ~15% margin
        var maxVal = if (maxDataVal > 0.0) maxDataVal * 1.15 else 0.0
        var minVal = if (minDataVal < 0.0) minDataVal * 1.15 else 0.0

        if (maxVal == 0.0 && minVal == 0.0) {
            maxVal = 10.0
            minVal = 0.0
        }

        val yRange = maxVal - minVal

        // Define paddings
        val paddingLeft = 140f
        val paddingRight = 40f
        val paddingTop = 60f
        val paddingBottom = 80f

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        fun getYForValue(value: Double): Float {
            val ratio = ((maxVal - value) / yRange).toFloat()
            return paddingTop + chartHeight * ratio
        }

        val zeroY = getYForValue(0.0)

        // Draw Y-axis grid lines and labels
        val paintText = Paint().apply {
            color = textColor.toArgb()
            textSize = 28f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        val gridLinesCount = 4
        for (i in 0..gridLinesCount) {
            val ratio = i.toFloat() / gridLinesCount
            val value = minVal + (maxVal - minVal) * ratio
            val y = getYForValue(value)

            val isZeroLine = abs(value) < (yRange * 0.02)

            // Draw horizontal grid line
            drawScope.drawLine(
                color = if (isZeroLine) axisColor else gridLineColor,
                start = Offset(paddingLeft, y),
                end = Offset(width - paddingRight, y),
                strokeWidth = if (isZeroLine) 3f else 1.5f
            )

            // Format Y label e.g., "+1.5k", "-500", "0"
            val label = formatYLabel(value)
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                label,
                paddingLeft - 15f,
                y + 9f,
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

        val paintBarValInside = Paint().apply {
            color = Color.White.toArgb()
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val paintBarValOutside = Paint().apply {
            color = textColor.toArgb()
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
            val yVal = getYForValue(amount)

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

            if (amount >= 0.0) {
                val barHeightVal = zeroY - yVal

                val brush = Brush.verticalGradient(
                    colors = listOf(positiveBarColorEnd, positiveBarColorStart),
                    startY = yVal,
                    endY = zeroY
                )

                if (barHeightVal > 0f) {
                    // Draw rounded rect bar (rounded top)
                    drawScope.drawRoundRect(
                        brush = brush,
                        topLeft = Offset(xStart, yVal),
                        size = Size(barWidth, barHeightVal),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    // Flatten the bottom of the rounded rectangle at baseline
                    if (barHeightVal > 12f) {
                        drawScope.drawRect(
                            brush = brush,
                            topLeft = Offset(xStart, zeroY - 12f),
                            size = Size(barWidth, 12f)
                        )
                    }

                    // Display amount text
                    val displayAmt = formatAmountLabel(amount)
                    if (barHeightVal > 45f) {
                        drawScope.drawContext.canvas.nativeCanvas.drawText(
                            displayAmt,
                            xStart + barWidth / 2f,
                            yVal + 30f,
                            paintBarValInside
                        )
                    } else if (barHeightVal > 5f) {
                        drawScope.drawContext.canvas.nativeCanvas.drawText(
                            displayAmt,
                            xStart + barWidth / 2f,
                            yVal - 8f,
                            paintBarValOutside
                        )
                    }
                }
            } else {
                val barHeightVal = yVal - zeroY

                val brush = Brush.verticalGradient(
                    colors = listOf(negativeBarColorStart, negativeBarColorEnd),
                    startY = zeroY,
                    endY = yVal
                )

                if (barHeightVal > 0f) {
                    // Draw rounded rect bar (rounded bottom)
                    drawScope.drawRoundRect(
                        brush = brush,
                        topLeft = Offset(xStart, zeroY),
                        size = Size(barWidth, barHeightVal),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    // Flatten the top of the rounded rectangle at baseline
                    if (barHeightVal > 12f) {
                        drawScope.drawRect(
                            brush = brush,
                            topLeft = Offset(xStart, zeroY),
                            size = Size(barWidth, 12f)
                        )
                    }

                    // Display amount text
                    val displayAmt = formatAmountLabel(amount)
                    if (barHeightVal > 45f) {
                        drawScope.drawContext.canvas.nativeCanvas.drawText(
                            displayAmt,
                            xStart + barWidth / 2f,
                            yVal - 15f,
                            paintBarValInside
                        )
                    } else if (barHeightVal > 5f) {
                        drawScope.drawContext.canvas.nativeCanvas.drawText(
                            displayAmt,
                            xStart + barWidth / 2f,
                            yVal + 25f,
                            paintBarValOutside
                        )
                    }
                }
            }

            // Draw X-axis label (month) at the bottom
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                displayDate,
                xStart + barWidth / 2f,
                paddingTop + chartHeight + 45f,
                paintXLabel
            )
        }
    }
}
