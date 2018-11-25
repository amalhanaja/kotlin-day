package io.github.amalhanaja.notes

import io.github.amalhanaja.utcDateTime
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import java.util.*

class NoteDao(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<NoteDao>(table = NoteTable)

    var title: String by NoteTable.title
    var body: String by NoteTable.body
    var createdAt: Long by NoteTable.createdAt
    var updatedAt: Long? by NoteTable.updatedAt
    var deletedAt: Long? by NoteTable.deletedAt

    fun toNote(): Note {
        return Note(
            id = id.value.toString(),
            title = title,
            body = body,
            createdAt = createdAt.utcDateTime,
            updatedAt = updatedAt?.utcDateTime,
            deletedAt = deletedAt?.utcDateTime
        )
    }

}