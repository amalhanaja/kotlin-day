package io.github.amalhanaja.notes

data class NoteResponse(
    val id: String,
    val title: String,
    val body: String,
    val date: String
)