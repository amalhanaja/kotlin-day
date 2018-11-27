package io.github.amalhanja.notes

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String = "",
    var title: String = "",
    var body: String = "",
    val date: String = ""
)