package pe.com.carwashperuapp.carwashapp.database

import android.icu.math.BigDecimal
import androidx.room.TypeConverter

class BigDecimalConverter {
    @TypeConverter
    fun bigDecimalToDouble(input: BigDecimal?): Double {
        return input?.toDouble() ?: 0.0
    }

    @TypeConverter
    fun stringToBigDecimal(input: Double?): BigDecimal {
        if (input == null) return BigDecimal.ZERO
        return BigDecimal.valueOf(input) ?: BigDecimal.ZERO
    }
}