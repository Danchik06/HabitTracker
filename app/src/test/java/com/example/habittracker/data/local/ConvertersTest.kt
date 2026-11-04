
package com.example.habittracker.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converter = Converters()

    @Test
    fun convertListToString() {
        val days = listOf("MONDAY", "WEDNESDAY", "FRIDAY")

        val result = converter.fromListToString(days)

        assertEquals("MONDAY,WEDNESDAY,FRIDAY", result)
    }

    @Test
    fun convertStringToList() {
        val data = "MONDAY,WEDNESDAY,FRIDAY"

        val result = converter.toListFromString(data)

        assertEquals(
            listOf("MONDAY", "WEDNESDAY", "FRIDAY"),
            result
        )
    }

    @Test
    fun convertEmptyListToString() {
        val result = converter.fromListToString(emptyList())

        assertEquals("", result)
    }

    @Test
    fun convertEmptyStringToList() {
        val result = converter.toListFromString("")

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun convertSingleItemList() {
        val days = listOf("MONDAY")

        val converted = converter.fromListToString(days)
        val result = converter.toListFromString(converted)

        assertEquals(days, result)
    }
}
