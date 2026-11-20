package com.savoj.shoppinglist

import com.savoj.shoppinglist.data.repository.ShoppingRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShoppingRepositoryTest {

    @Test
    fun testCleanItemName_stripsHyphenBullet() {
        val input = "- Молоко"
        val output = ShoppingRepositoryImpl.cleanItemName(input)
        assertEquals("Молоко", output)
    }

    @Test
    fun testCleanItemName_stripsStarBullet() {
        val input = "* Хліб житній"
        val output = ShoppingRepositoryImpl.cleanItemName(input)
        assertEquals("Хліб житній", output)
    }

    @Test
    fun testCleanItemName_stripsDotBullet() {
        val input = "• Яблука Голден"
        val output = ShoppingRepositoryImpl.cleanItemName(input)
        assertEquals("Яблука Голден", output)
    }

    @Test
    fun testCleanItemName_stripsNumberedPrefix() {
        val input1 = "1. Сир Чеддер"
        val input2 = "12) Кава в зернах"
        assertEquals("Сир Чеддер", ShoppingRepositoryImpl.cleanItemName(input1))
        assertEquals("Кава в зернах", ShoppingRepositoryImpl.cleanItemName(input2))
    }

    @Test
    fun testParseMultiLineText_newlines() {
        val raw = """
            - Молоко 2.5%
            * Хліб
            
            1. Яйця 10 шт
            • Масло
        """.trimIndent()

        val result = ShoppingRepositoryImpl.parseMultiLineText(raw)
        assertEquals(4, result.size)
        assertEquals("Молоко 2.5%", result[0])
        assertEquals("Хліб", result[1])
        assertEquals("Яйця 10 шт", result[2])
        assertEquals("Масло", result[3])
    }

    @Test
    fun testParseMultiLineText_commaSeparated() {
        val raw = "Помідори, Огірки, Перець, Зелень"
        val result = ShoppingRepositoryImpl.parseMultiLineText(raw)
        assertEquals(4, result.size)
        assertEquals("Помідори", result[0])
        assertEquals("Огірки", result[1])
        assertEquals("Перець", result[2])
        assertEquals("Зелень", result[3])
    }

    @Test
    fun testParseMultiLineText_empty() {
        val raw = "   \n   \n   "
        val result = ShoppingRepositoryImpl.parseMultiLineText(raw)
        assertTrue(result.isEmpty())
    }
}
