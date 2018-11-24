package io.github.amalhanaja.notes

import com.google.gson.Gson
import io.github.amalhanaja.*
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class NoteRouterTest {

    private val validNote = Note(title = "TITLE", body = "BODY")
    private val invalidNote = Note()

    @BeforeTest
    fun setUp() {
        mockkObject(NoteService)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun notes() = withTestServer {
        val list: List<List<NoteResponse>> = listOf(emptyList(), listOf(SampleObject.noteResponse))
        every { NoteService.list() } returnsMany list
        list.forEach { expected ->
            handleRequest(HttpMethod.Get, "/notes").apply {
                response.expectStatusCode(HttpStatusCode.OK)
                response.expectJson(expected)
            }
        }
        verify(exactly = 2) { NoteService.list() }
    }

    @Test
    fun noteById() = withTestServer {
        every { NoteService.noteById("ID") } returns SampleObject.noteResponse
        handleRequest(HttpMethod.Get, "/notes/ID").apply {
            response.expectStatusCode(HttpStatusCode.OK)
            response.expectJson(SampleObject.noteResponse)
        }
        every { NoteService.noteById("NO_ID") } throws UnsupportedOperationException()
        handleRequest(HttpMethod.Get, "/notes/NO_ID").apply {
            response.expectStatusCode(HttpStatusCode.UnprocessableEntity)
            expect(true) { response.content.isNullOrEmpty() }
        }
        verifyOrder {
            NoteService.noteById("ID")
            NoteService.noteById("NO_ID")
        }
    }

    @Test
    fun createNote() = withTestServer {
        every { NoteService.createNote(validNote) }.returns(Unit)
        handleRequest(HttpMethod.Post, "/notes") { setBody(validNote.toParamBody()) }.apply {
            response.expectEmptyResponse()
            response.expectStatusCode(HttpStatusCode.Created)
        }
        every { NoteService.createNote(invalidNote) } throws IllegalArgumentException()
        handleRequest(HttpMethod.Post, "/notes") { setBody(invalidNote.toParamBody()) }.apply {
            response.expectEmptyResponse()
            response.expectStatusCode(HttpStatusCode.UnprocessableEntity)
        }
        verifyOrder {
            NoteService.createNote(validNote)
            NoteService.createNote(invalidNote)
        }
    }

    @Test
    fun updateNote() = withTestServer {
        every { NoteService.updateNote(validNote.copy(id = "ID")) } returns Unit
        handleRequest(HttpMethod.Put, "/notes/ID") { setBody(validNote.toParamBody()) }.apply {
            response.expectEmptyResponse()
            response.expectStatusCode(HttpStatusCode.OK)

        }
        every { NoteService.updateNote(invalidNote.copy(id = "NO_ID")) } throws IllegalArgumentException()
        handleRequest(HttpMethod.Put, "/notes/NO_ID") { setBody(invalidNote.toParamBody()) }.apply {
            response.expectEmptyResponse()
            response.expectStatusCode(HttpStatusCode.UnprocessableEntity)
        }
        verifyOrder {
            NoteService.updateNote(validNote.copy(id = "ID"))
            NoteService.updateNote(invalidNote.copy(id = "NO_ID"))
        }
    }

    @Test
    fun deleteNote() = withTestServer {
        every { NoteService.deleteNote("ID") } returns Unit
        handleRequest(HttpMethod.Delete, "/notes/ID").apply {
            response.expectEmptyResponse()
            response.expectStatusCode(HttpStatusCode.OK)
        }
        every { NoteService.deleteNote("NO_ID") } throws IllegalArgumentException()
        handleRequest(HttpMethod.Delete, "/notes/NO_ID").apply {
            response.expectEmptyResponse()
            response.expectStatusCode(HttpStatusCode.UnprocessableEntity)
        }
        verifyOrder {
            NoteService.deleteNote("ID")
            NoteService.deleteNote("NO_ID")
        }
    }

    private fun Note.toParamBody(): String {
        return Gson().toJson(NoteParam(title, body))
    }
}