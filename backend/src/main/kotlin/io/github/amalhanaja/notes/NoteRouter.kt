package io.github.amalhanaja.notes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.notesRouter() {
    route("/notes") {
        get {
            runCatching {
                NoteService.list()
            }.onSuccess { res ->
                call.respond(HttpStatusCode.OK, res)
            }.onFailure {
                call.respond(HttpStatusCode.OK, emptyList<NoteResponse>())
            }
        }

        post {
            runCatching {
                val param = call.receive<NoteParam>()
                NoteService.createNote(Note(title = param.title, body = param.body))
            }.onSuccess {
                call.respond(HttpStatusCode.Created)
            }.onFailure {
                call.respond(HttpStatusCode.UnprocessableEntity)
            }
        }

        route("/{noteId}") {
            get {
                runCatching {
                    NoteService.noteById(call.parameters["noteId"]!!)
                }.onSuccess { res ->
                    call.respond(HttpStatusCode.OK, res)
                }.onFailure {
                    call.respond(HttpStatusCode.UnprocessableEntity)
                }
            }
            put {
                runCatching {
                    val param = call.receive<NoteParam>()
                    NoteService.updateNote(
                        Note(
                            id = call.parameters["noteId"]!!,
                            title = param.title,
                            body = param.body
                        )
                    )
                }.onSuccess {
                    call.respond(HttpStatusCode.OK)
                }.onFailure {
                    call.respond(HttpStatusCode.UnprocessableEntity)
                }
            }

            delete {
                runCatching {
                    NoteService.deleteNote(call.parameters["noteId"]!!)
                }.onSuccess {
                    call.respond(HttpStatusCode.OK)
                }.onFailure { call.respond(HttpStatusCode.UnprocessableEntity) }
            }
        }
    }
}