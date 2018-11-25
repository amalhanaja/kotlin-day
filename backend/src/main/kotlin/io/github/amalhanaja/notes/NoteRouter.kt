package io.github.amalhanaja.notes

import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.error

fun Route.notesRouter() {
    route("/notes") {
        get {
            runCatching {
                NoteService.list().map(Note::asResponse)
            }.onSuccess { res ->
                call.respond(HttpStatusCode.OK, res)
            }.onFailure { err ->
                application.log.error(err)
                call.respond(HttpStatusCode.OK, emptyList<NoteResponse>())
            }
        }

        post {
            runCatching {
                val param: NoteParam = call.receive()
                NoteService.createNote(Note(title = param.title, body = param.body))
            }.onSuccess {
                call.respond(HttpStatusCode.Created)
            }.onFailure { err ->
                application.log.error(err)
                call.respond(HttpStatusCode.UnprocessableEntity)
            }
        }

        route("/{noteId}") {
            get {
                runCatching {
                    NoteService.noteById(call.parameters["noteId"]!!).asResponse
                }.onSuccess { res ->
                    call.respond(HttpStatusCode.OK, res)
                }.onFailure { err ->
                    application.log.error(err)
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
                }.onFailure { err ->
                    application.log.error(err)
                    call.respond(HttpStatusCode.UnprocessableEntity)
                }
            }

            delete {
                runCatching {
                    NoteService.deleteNote(call.parameters["noteId"]!!)
                }.onSuccess {
                    call.respond(HttpStatusCode.OK)
                }.onFailure { err ->
                    application.log.error(err)
                    call.respond(HttpStatusCode.UnprocessableEntity)
                }
            }
        }
    }
}