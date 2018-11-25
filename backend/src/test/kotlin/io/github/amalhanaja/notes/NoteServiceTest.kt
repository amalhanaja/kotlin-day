package io.github.amalhanaja.notes

import io.github.amalhanaja.SampleObject
import io.mockk.*
import org.jetbrains.exposed.sql.SizedCollection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


class NoteServiceTest {

    private val mockNoteDao = mockk<NoteDao>()
    private val note = Note(title = "TITLE", body = "BODY")

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
        every { NoteDao.all() } returns SizedCollection(listOf(mockNoteDao))
        NoteService.list()
        verifyOrder {
            NoteDao.all()
            mockNoteDao.toNote()
        }
    }

    @Test
    fun noteById() {
        val uuid = SampleObject.uuid
        every { NoteDao[uuid] } returns mockNoteDao
        NoteService.noteById(uuid.toString())
        verifyOrder {
            NoteDao[SampleObject.uuid]
            mockNoteDao.toNote()
        }
    }

    @Test
    fun createNote() {
        every { NoteDao.new(any(), any()) } returns mockNoteDao
        NoteService.createNote(note.copy(SampleObject.uuid.toString()))
        verify { NoteDao.new(any(), any()) }
    }

    @Test
    fun updateNote() {
        every { NoteDao[SampleObject.uuid] } returns mockNoteDao
        every { mockNoteDao.title = any() } returns Unit
        every { mockNoteDao.body = any() } returns Unit
        every { mockNoteDao.updatedAt = any() } returns Unit
        NoteService.updateNote(note.copy(SampleObject.uuid.toString()))
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
        every { mockNoteDao.deletedAt = any() } returns Unit
        every { mockNoteDao.updatedAt = any() } returns Unit
        NoteService.deleteNote(SampleObject.uuid.toString())
        verifyOrder {
            NoteDao[SampleObject.uuid]
            mockNoteDao.deletedAt = any()
        }
    }
}