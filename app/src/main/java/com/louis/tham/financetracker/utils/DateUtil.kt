package com.louis.tham.financetracker.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtil {
    fun getCurrentDateDisplay(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        return currentDate.format(formatter)
    }

    fun convertMillisToString(millis: Long, pattern: String = "yyyy-MM-dd"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        return try {
            sdf.format(Date(millis))
        } catch (e: Exception) {
            sdf.format(Date(System.currentTimeMillis()))
        }
    }

    fun convertStringToMillis(dateString: String, pattern: String = "yyyy-MM-dd"): Long {
        return try {
            // 1. Parse the string using the same format pattern
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            val date = sdf.parse(dateString) ?: return System.currentTimeMillis()

            // 2. Read the parsed year, month, and day values using a local calendar
            val localCalendar = Calendar.getInstance().apply {
                time = date
            }

            // 3. Map those exact calendar values directly onto a UTC Calendar
            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                clear()
                set(Calendar.YEAR, localCalendar.get(Calendar.YEAR))
                set(Calendar.MONTH, localCalendar.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, localCalendar.get(Calendar.DAY_OF_MONTH))
            }

            utcCalendar.timeInMillis
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}