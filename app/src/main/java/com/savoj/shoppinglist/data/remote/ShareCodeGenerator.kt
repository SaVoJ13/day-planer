package com.savoj.shoppinglist.data.remote

import java.security.SecureRandom

object ShareCodeGenerator {
    // Exclude confusing characters: 0, O, 1, I, L
    private const val CHARSET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ"
    private val random = SecureRandom()

    fun generateCode(length: Int = 6): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            val idx = random.nextInt(CHARSET.length)
            sb.append(CHARSET[idx])
        }
        return sb.toString()
    }

    fun normalizeCode(input: String): String {
        return input.trim().uppercase().replace(" ", "").replace("-", "")
    }

    fun isValidCode(code: String): Boolean {
        val normalized = normalizeCode(code)
        if (normalized.length != 6) return false
        return normalized.all { CHARSET.contains(it) }
    }
}
