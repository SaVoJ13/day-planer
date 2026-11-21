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

    @Test
    fun testShoppingList_wishlistDefaults_isFalseAndNull() {
        val list = com.savoj.shoppinglist.domain.model.ShoppingList(
            id = 10L,
            name = "Звичайний список",
            createdAt = 2000L
        )
        org.junit.Assert.assertFalse(list.isWishlist)
        org.junit.Assert.assertNull(list.occasion)
    }

    @Test
    fun testShoppingList_wishlistExplicit_properties() {
        val wishlist = com.savoj.shoppinglist.domain.model.ShoppingList(
            id = 11L,
            name = "Мій День народження",
            createdAt = 2000L,
            isWishlist = true,
            occasion = "🎂 День народження"
        )
        org.junit.Assert.assertTrue(wishlist.isWishlist)
        assertEquals("🎂 День народження", wishlist.occasion)
    }

    @Test
    fun testShoppingItem_wishlistDefaults_isFalseAndNull() {
        val item = com.savoj.shoppinglist.domain.model.ShoppingItem(
            id = 100L,
            listId = 11L,
            name = "Навушники Sony"
        )
        org.junit.Assert.assertFalse(item.isReserved)
        org.junit.Assert.assertNull(item.reservedBy)
        org.junit.Assert.assertNull(item.url)
    }

    @Test
    fun testShoppingItem_reservationToggle() {
        val gift = com.savoj.shoppinglist.domain.model.ShoppingItem(
            id = 101L,
            listId = 11L,
            name = "Книга про Kotlin",
            url = "https://example.com/kotlin-book"
        )
        org.junit.Assert.assertFalse(gift.isReserved)
        org.junit.Assert.assertNull(gift.reservedBy)
        assertEquals("https://example.com/kotlin-book", gift.url)

        val reservedGift = gift.copy(isReserved = true, reservedBy = "Олександр")
        org.junit.Assert.assertTrue(reservedGift.isReserved)
        assertEquals("Олександр", reservedGift.reservedBy)

        val unreservedGift = reservedGift.copy(isReserved = false, reservedBy = null)
        org.junit.Assert.assertFalse(unreservedGift.isReserved)
        org.junit.Assert.assertNull(unreservedGift.reservedBy)
    }

    @Test
    fun testShoppingListEntity_wishlistDefaults() {
        val entity = com.savoj.shoppinglist.data.local.entity.ShoppingListEntity(
            id = 20L,
            name = "Подарунки на Новий Рік",
            createdAt = 3000L,
            isWishlist = true,
            occasion = "🎄 Новий рік / Різдво"
        )
        org.junit.Assert.assertTrue(entity.isWishlist)
        assertEquals("🎄 Новий рік / Різдво", entity.occasion)
    }

    @Test
    fun testShoppingItemEntity_wishlistDefaults() {
        val entity = com.savoj.shoppinglist.data.local.entity.ShoppingItemEntity(
            id = 200L,
            listId = 20L,
            name = "Lego Star Wars",
            url = "https://rozetka.com.ua/lego",
            isReserved = true,
            reservedBy = "Марія"
        )
        org.junit.Assert.assertTrue(entity.isReserved)
        assertEquals("Марія", entity.reservedBy)
        assertEquals("https://rozetka.com.ua/lego", entity.url)
    }
}
