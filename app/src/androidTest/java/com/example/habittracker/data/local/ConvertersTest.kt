
package com.example.habittracker.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converter = Converters()

    @Test
    fun fromListToString_convertsListCorrectly() {
        val days = listOf("Monday", "Wednesday", "Friday")

        val result = converter.fromListToString(days)

        assertEquals("Monday,Wednesday,Friday", result)
    }

    @Test
    fun toListFromString_convertsStringCorrectly() {
        val data = "Monday,Wednesday,Friday"

        val result = converter.toListFromString(data)

        assertEquals(
            listOf("Monday", "Wednesday", "Friday"),
            result
        )
    }

    @Test
    fun toListFromString_returnsEmptyList_whenStringIsEmpty() {
        val result = converter.toListFromString("")

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun converter_roundTrip_preservesList() {
        val original = listOf("Monday", "Tuesday", "Sunday")

        val converted = converter.fromListToString(original)
        val result = converter.toListFromString(converted)

        assertEquals(original, result)
    }
}
