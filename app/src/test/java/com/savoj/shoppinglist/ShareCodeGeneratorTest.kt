package com.savoj.shoppinglist

import com.savoj.shoppinglist.data.remote.ShareCodeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareCodeGeneratorTest {

    @Test
    fun testGenerateCode_lengthAndFormat() {
        val code = ShareCodeGenerator.generateCode()
        assertEquals(6, code.length)
        assertTrue(ShareCodeGenerator.isValidCode(code))
    }

    @Test
    fun testGenerateCode_excludesConfusingCharacters() {
        for (i in 0 until 100) {
            val code = ShareCodeGenerator.generateCode()
            assertFalse("Code must not contain confusing '0'", code.contains('0'))
            assertFalse("Code must not contain confusing 'O'", code.contains('O'))
            assertFalse("Code must not contain confusing '1'", code.contains('1'))
            assertFalse("Code must not contain confusing 'I'", code.contains('I'))
            assertFalse("Code must not contain confusing 'L'", code.contains('L'))
        }
    }

    @Test
    fun testNormalizeCode_removesSpacesAndHyphens() {
        val input = " a-b 2 3 c "
        val normalized = ShareCodeGenerator.normalizeCode(input)
        assertEquals("AB23C", normalized)
    }

    @Test
    fun testIsValidCode_validation() {
        assertTrue(ShareCodeGenerator.isValidCode("K7P9X2"))
        assertTrue(ShareCodeGenerator.isValidCode("k7p-9x2")) // normalized valid
        assertFalse(ShareCodeGenerator.isValidCode("K7P9X")) // too short
        assertFalse(ShareCodeGenerator.isValidCode("K7P9X23")) // too long
        assertFalse(ShareCodeGenerator.isValidCode("K7P9X0")) // contains '0'
        assertFalse(ShareCodeGenerator.isValidCode("K7P9XO")) // contains 'O'
    }
}
