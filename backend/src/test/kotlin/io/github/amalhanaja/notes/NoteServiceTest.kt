package io.github.amalhanaja.notes

import io.github.amalhanaja.SampleObject
import io.github.amalhanaja.connectTestDatabase
import io.github.amalhanaja.isNone
import io.github.amalhanaja.utcDateTime
import io.mockk.*
import org.jetbrains.exposed.sql.SizedCollection
import org.junit.BeforeClass
import java.util.*
import kotlin.test.*


class NoteServiceTest {

    private val mockNoteDao = mockk<NoteDao>()
    private val note = Note(title = "TITLE", body = "BODY")

    companion object {
        @BeforeClass
        @JvmStatic
        fun initialize() {
            connectTestDatabase()
        }
    }

    @BeforeTest
    fun setUp() {
        mockkObject(NoteDao.Companion)
        every { mockNoteDao.toNote() } returns Note()
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun list() {
        val notes = (0 until 10).map {
            Note(
                id = UUID.randomUUID().toString(),
                title = "TITLE #$it",
                body = "BODY #$it",
                createdAt = utcDateTime.minusDays(it),
                deletedAt = if (it % 2 == 0) utcDateTime else null
            )
        }
        every { mockNoteDao.toNote() } returnsMany notes
        every { NoteDao.all() } returns SizedCollection(notes.map { mockNoteDao })
        val list = NoteService.list()
        expect(5) { list.count() }
        list.zipWithNext { a, b ->
            expect(true) { a.createdAt!!.isAfter(b.createdAt) }
            expect(true) { a.deletedAt.isNone && b.deletedAt.isNone }
        }
        verifyOrder {
            NoteDao.all()
            mockNoteDao.toNote()
        }
    }

    @Test
    fun noteById() {
        val uuid = SampleObject.uuid
        every { NoteDao[uuid] } returns mockNoteDao
        every { mockNoteDao.deletedAt } returns null andThen utcDateTime.millis
        kotlin.runCatching { NoteService.noteById(uuid.toString()) }.onFailure { fail("should Success") }
        kotlin.runCatching { NoteService.noteById(uuid.toString()) }.onSuccess { fail("should Fail") }
        verifyOrder {
            NoteDao[SampleObject.uuid]
            mockNoteDao.deletedAt
            mockNoteDao.toNote()
        }
    }

    @Test
    fun createNote() {
        val slot: MutableList<NoteDao.() -> Unit> = mutableListOf()
        every { NoteDao.new(any(), capture(slot)) } returns mockNoteDao
        every { mockNoteDao.title = any() } returns Unit
        every { mockNoteDao.body = any() } returns Unit
        kotlin.runCatching { NoteService.createNote(note) }
            .onFailure { fail("should Success") }
        kotlin.runCatching { NoteService.createNote(note.copy(title = "", body = "")) }
            .onSuccess { fail("should Fail") }
        kotlin.runCatching { NoteService.createNote(note.copy(title = "")) }
            .onSuccess { fail("should Fail") }
        kotlin.runCatching { NoteService.createNote(note.copy(body = "")) }
            .onSuccess { fail("should Fail") }
        expect(1) { slot.count() }
        verify(exactly = 1) { NoteDao.new(any(), any()) }
    }

    @Test
    fun updateNote() {
        every { NoteDao[SampleObject.uuid] } returns mockNoteDao
        every { mockNoteDao.title = any() } returns Unit
        every { mockNoteDao.body = any() } returns Unit
        every { mockNoteDao.updatedAt = any() } returns Unit
        every { mockNoteDao.deletedAt } returns null andThen utcDateTime.millis
        kotlin.runCatching { NoteService.updateNote(note.copy(SampleObject.uuid.toString())) }
            .onFailure { fail("should Success") }
        kotlin.runCatching { NoteService.updateNote(note.copy(SampleObject.uuid.toString())) }
            .onSuccess { fail("should Fail") }
        verifyOrder {
            NoteDao[SampleObject.uuid]
            mockNoteDao.title = any()
            mockNoteDao.body = any()
            mockNoteDao.updatedAt = any()
        }
    }

    @Test
    fun deleteNote() {
        every { NoteDao[SampleObject.uuid] } returns mockNoteDao
        every { mockNoteDao.deletedAt } returns null andThen utcDateTime.millis
        every { mockNoteDao.deletedAt = any() } returns Unit
        every { mockNoteDao.updatedAt = any() } returns Unit
        kotlin.runCatching { NoteService.deleteNote(SampleObject.uuid.toString()) }.onFailure { fail("should Success") }
        kotlin.runCatching { NoteService.deleteNote(SampleObject.uuid.toString()) }.onSuccess { fail("should Fail") }
        verifyOrder {
            NoteDao[SampleObject.uuid]
            mockNoteDao.deletedAt = any()
            mockNoteDao.updatedAt = any()
        }
    }
}