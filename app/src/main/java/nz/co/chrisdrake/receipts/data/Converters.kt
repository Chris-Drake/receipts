package nz.co.chrisdrake.receipts.data

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }
}
