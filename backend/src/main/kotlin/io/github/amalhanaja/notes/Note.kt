package io.github.amalhanaja.notes

import org.joda.time.DateTime

data class Note(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val createdAt: DateTime? = null,
    val updatedAt: DateTime? = null,
    val deletedAt: DateTime? = null
)