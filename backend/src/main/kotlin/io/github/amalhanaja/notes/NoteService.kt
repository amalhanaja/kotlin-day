package io.github.amalhanaja.notes

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object NoteService {
    fun list(): List<NoteResponse> {
        return (1 until 10).map { int ->
            NoteResponse(
                "#$int",
                "Title #$int",
                "Body #$int",
                DateTime.now(DateTimeZone.UTC).toString()
            )
        }
    }

    fun noteById(id: String): NoteResponse {
        return list().first { it.id == id }
    }

    fun createNote(note: Note) {
    }

    fun updateNote(note: Note) {
    }

    fun deleteNote(id: String) {
    }

}
