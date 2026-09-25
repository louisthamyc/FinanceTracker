package com.louis.tham.financetracker.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class DateUtilTest {

    @Test
    fun getCurrentDateDisplay_matchesCurrentLocalDate() {
        val result = DateUtil.getCurrentDateDisplay()
        val expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))

        assertEquals(expected, result)
        assertTrue(result.matches(Regex("""^\d{4}-\d{2}-\d{2}$""")))
    }

    @Test
    fun convertMillisToString_formatsDefaultPatternCorrectly() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 25, 12, 0, 0)
        }
        val millis = calendar.timeInMillis

        val formatted = DateUtil.convertMillisToString(millis)
        assertEquals("2026-03-25", formatted)
    }

    @Test
    fun convertMillisToString_formatsCustomPatternCorrectly() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.DECEMBER, 31, 15, 30, 0)
        }
        val millis = calendar.timeInMillis

        val formatted = DateUtil.convertMillisToString(millis, "yyyy/MM/dd")
        assertEquals("2026/12/31", formatted)
    }

    @Test
    fun convertStringToMillis_parsesValidDateStringToUtcMillis() {
        val dateStr = "2026-07-20"
        val millis = DateUtil.convertStringToMillis(dateStr)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = millis
        }

        assertEquals(2026, calendar.get(Calendar.YEAR))
        assertEquals(Calendar.JULY, calendar.get(Calendar.MONTH))
        assertEquals(20, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun convertStringToMillis_withInvalidDate_handlesGracefullyWithoutException() {
        val before = System.currentTimeMillis()
        val millis = DateUtil.convertStringToMillis("invalid-date-string")
        val after = System.currentTimeMillis()

        assertTrue(millis in before..after)
    }

    @Test
    fun convertStringToMillis_withCustomPattern_parsesCorrectly() {
        val dateStr = "20/07/2026"
        val millis = DateUtil.convertStringToMillis(dateStr, "dd/MM/yyyy")

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = millis
        }

        assertEquals(2026, calendar.get(Calendar.YEAR))
        assertEquals(Calendar.JULY, calendar.get(Calendar.MONTH))
        assertEquals(20, calendar.get(Calendar.DAY_OF_MONTH))
    }
}
