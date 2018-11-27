package io.github.amalhanja

import io.github.amalhanja.notes.NotesPage
import kotlinx.serialization.ImplicitReflectionSerializer
import react.dom.render
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import kotlin.browser.document
import kotlin.browser.window

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
    window.onload = {
        render(document.getElementById("root")) {
            browserRouter {
                switch {
                    route("/", exact = true, component = NotesPage::class)
                }
            }
        }
    }
}