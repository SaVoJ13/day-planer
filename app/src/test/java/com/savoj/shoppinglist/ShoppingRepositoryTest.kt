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
    fun testParseMultiLineText_conjunctions() {
        val raw = "Хліб і молоко та сир й масло and яйця"
        val result = ShoppingRepositoryImpl.parseMultiLineText(raw)
        assertEquals(5, result.size)
        assertEquals("Хліб", result[0])
        assertEquals("молоко", result[1])
        assertEquals("сир", result[2])
        assertEquals("масло", result[3])
        assertEquals("яйця", result[4])
    }

    @Test
    fun testParseMultiLineText_semicolonsAndMixed() {
        val raw = "Кава; Чай, Цукор\nПечиво"
        val result = ShoppingRepositoryImpl.parseMultiLineText(raw)
        assertEquals(4, result.size)
        assertEquals("Кава", result[0])
        assertEquals("Чай", result[1])
        assertEquals("Цукор", result[2])
        assertEquals("Печиво", result[3])
    }

    @Test
    fun testParseMultiLineText_empty() {
        val raw = "   \n   \n   "
        val result = ShoppingRepositoryImpl.parseMultiLineText(raw)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testShoppingList_defaultIsLocked_isFalse() {
        val list = com.savoj.shoppinglist.domain.model.ShoppingList(
            id = 1L,
            name = "Продукти",
            createdAt = 1000L
        )
        org.junit.Assert.assertFalse(list.isLocked)
    }

    @Test
    fun testShoppingList_isLocked_toggle() {
        val list = com.savoj.shoppinglist.domain.model.ShoppingList(
            id = 1L,
            name = "Продукти",
            createdAt = 1000L,
            isLocked = false
        )
        val lockedList = list.copy(isLocked = true)
        org.junit.Assert.assertTrue(lockedList.isLocked)
    }

    @Test
    fun testShoppingListEntity_defaultIsLocked_isFalse() {
        val entity = com.savoj.shoppinglist.data.local.entity.ShoppingListEntity(
            id = 1L,
            name = "Продукти",
            createdAt = 1000L
        )
        org.junit.Assert.assertFalse(entity.isLocked)
    }
}
