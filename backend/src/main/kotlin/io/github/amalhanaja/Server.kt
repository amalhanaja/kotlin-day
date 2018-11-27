package io.github.amalhanaja

import io.github.amalhanaja.db.DBConfig
import io.github.amalhanaja.notes.notesRouter
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    if (!testing) {
        DBConfig.init(this)
    }
    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Post)
        method(HttpMethod.Options)
        method(HttpMethod.Delete)
        anyHost()
    }
    install(ContentNegotiation) { gson() }
    routing {
        notesRouter()
        get("/") {
            call.respond("Hello World")
        }
    }
}