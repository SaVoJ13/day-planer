package com.savoj.shoppinglist.data.remote.model

import com.google.firebase.Timestamp

data class SharedListDto(
    val id: String = "",
    val name: String = "",
    val shareCode: String = "",
    val creatorId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
