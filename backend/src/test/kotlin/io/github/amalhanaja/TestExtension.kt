package io.github.amalhanaja

import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.withTestApplication
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