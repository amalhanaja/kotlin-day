package io.github.amalhanja.http

import kotlinx.serialization.ImplicitReflectionSerializer
import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@UseExperimental(ImplicitReflectionSerializer::class)
suspend inline fun http(
    method: HttpMethod,
    url: String,
    body: String? = null,
    headers: Map<String, String> = mapOf()
): String {
    return suspendCoroutine { coroutine: Continuation<String> ->
        val xhr = XMLHttpRequest()
        xhr.onreadystatechange = {
            if (xhr.readyState == XMLHttpRequest.DONE) {
                if (xhr.status in 200 until 300) coroutine.resume(xhr.responseText)
                else coroutine.resumeWithException(HttpException(xhr.status, xhr.responseText))
            }
        }
        xhr.open(method.name, url)
        headers.forEach { (key: String, value: String) -> xhr.setRequestHeader(key, value) }
        runCatching { requireNotNull(body) }.onSuccess { xhr.send(it) }.onFailure { xhr.send() }
    }
}

data class HttpException(val status: Short, val responseText: String) : Error("HttpException($status, $responseText)")

enum class HttpMethod { GET, POST, PATCH, PUT, OPTIONS, DELETE }