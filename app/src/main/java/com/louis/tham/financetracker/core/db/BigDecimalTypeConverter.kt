package com.louis.tham.financetracker.core.db

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalTypeConverter {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String = value.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal = value.toBigDecimal()
}
