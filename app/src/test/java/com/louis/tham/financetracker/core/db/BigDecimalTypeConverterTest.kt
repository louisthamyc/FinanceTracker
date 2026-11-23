package com.louis.tham.financetracker.core.db

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class BigDecimalTypeConverterTest {
    private val converter = BigDecimalTypeConverter()

    @Test
    fun convertsBigDecimalToPlainStringAndBackWithoutLosingScale() {
        val original = BigDecimal("12345678901234567890.4500")

        val stored = converter.fromBigDecimal(original)
        val restored = converter.toBigDecimal(stored)

        assertEquals("12345678901234567890.4500", stored)
        assertEquals(original, restored)
    }
}
