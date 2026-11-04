package com.example.habittracker.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return list.joinToString(",")
    }
    @TypeConverter
    fun toListFromString(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }
}