package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.OvertimeType

class Converters {
  @TypeConverter
  fun fromOvertimeType(type: OvertimeType): String {
    return type.name
  }

  @TypeConverter
  fun toOvertimeType(value: String): OvertimeType {
    return try {
      OvertimeType.valueOf(value)
    } catch (e: Exception) {
      OvertimeType.WORKDAY
    }
  }
}
