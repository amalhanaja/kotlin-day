package io.github.amalhanaja

import com.google.gson.Gson
import io.github.amalhanaja.notes.NoteTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.withTestApplication
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.expect

fun withTestServer(test: TestApplicationEngine.() -> Unit) {
    return withTestApplication(moduleFunction = { module(testing = true) }, test = test)
}

inline fun <reified T : Any> TestApplicationResponse.json(): T {
    return Gson().fromJson(content, T::class.javaObjectType)
}

inline fun <reified T : Any> TestApplicationResponse.expectJson(expected: T) {
    val expectedJson = Gson().toJson(expected)
    expect(expectedJson) { content }
}

fun TestApplicationResponse.expectStatusCode(statusCode: HttpStatusCode) {
    expect(statusCode) { status() }
}

fun TestApplicationResponse.expectEmptyResponse() {
    expect(true) { content.isNullOrEmpty() }
}

fun connectTestDatabase() {
    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver").also {
        transaction(it) {
            SchemaUtils.create(NoteTable)
        }
    }
}