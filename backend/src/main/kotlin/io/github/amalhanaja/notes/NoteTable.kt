package io.github.amalhanaja.notes

import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.Column

object NoteTable : UUIDTable(name = "notes", columnName = "uuid") {
    val title: Column<String> = varchar(name = "title", length = 128)
    val body: Column<String> = text(name = "body")
    val createdAt: Column<Long> = long(name = "createdAt")
    val updatedAt: Column<Long?> = long(name = "updatedAt").nullable()
    val deletedAt: Column<Long?> = long(name = "deletedAt").nullable()
}