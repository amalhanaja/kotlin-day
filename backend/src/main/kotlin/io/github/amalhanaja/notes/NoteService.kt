package io.github.amalhanaja.notes

import io.github.amalhanaja.db.DBConfig
import io.github.amalhanaja.isNone
import io.github.amalhanaja.isPresent
import io.github.amalhanaja.utcDateTime
import io.github.amalhanaja.uuid
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object NoteService {
    fun list(): List<Note> {
        return transaction(DBConfig.db) {
            return@transaction NoteDao.all()
                .map(NoteDao::toNote)
                .filter { it.deletedAt.isNone }
                .sortedByDescending(Note::createdAt)
        }
    }

    @Throws(UnsupportedOperationException::class)
    fun noteById(id: String): Note {
        return transaction(DBConfig.db) {
            val note = NoteDao[id.uuid]
            if (note.deletedAt.isPresent) throw UnsupportedOperationException("Already Deleted")
            return@transaction note.toNote()
        }
    }

    fun createNote(note: Note) {
        if (note.title.isBlank() || note.body.isBlank()) {
            throw UnsupportedOperationException("Title and Body can't be Blank")
        }
        transaction(DBConfig.db) {
            NoteDao.new(UUID.randomUUID()) {
                title = note.title
                body = note.body
                createdAt = utcDateTime.millis
            }
        }
    }

    fun updateNote(note: Note) {
        transaction(DBConfig.db) {
            NoteDao[note.id.uuid].apply {
                if (deletedAt.isPresent) throw UnsupportedOperationException("Already Deleted")
                title = note.title
                body = note.body
                updatedAt = utcDateTime.millis
            }
        }
    }

    fun deleteNote(id: String) {
        transaction(DBConfig.db) {
            NoteDao[id.uuid].apply {
                if (deletedAt.isPresent) throw UnsupportedOperationException("Already Deleted")
                deletedAt = utcDateTime.millis
                updatedAt = utcDateTime.millis
            }
        }
    }

}
