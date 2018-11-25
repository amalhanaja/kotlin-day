package io.github.amalhanaja.notes

import io.github.amalhanaja.utcDateTime
import io.github.amalhanaja.uuid

object NoteService {
    fun list(): List<Note> {
        return NoteDao.all().map(NoteDao::toNote)
    }

    fun noteById(id: String): Note {
        return NoteDao[id.uuid].toNote()
    }

    fun createNote(note: Note) {
        NoteDao.new(note.id.uuid) {
            title = note.title
            body = note.body
            createdAt = utcDateTime.millis
        }
    }

    fun updateNote(note: Note) {
        NoteDao[note.id.uuid].apply {
            title = note.title
            body = note.body
            updatedAt = utcDateTime.millis
        }
    }

    fun deleteNote(id: String) {
        NoteDao[id.uuid].apply {
            deletedAt = utcDateTime.millis
            updatedAt = utcDateTime.millis
        }
    }

}
