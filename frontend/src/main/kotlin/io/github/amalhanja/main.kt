package io.github.amalhanja

import react.buildElement
import react.dom.h1
import react.dom.render
import react.router.dom.hashRouter
import react.router.dom.route
import react.router.dom.switch
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    window.onload = {
        render(document.getElementById("root")) {
            hashRouter {
                switch {
                    route("/") {
                        buildElement {
                            h1 {
                                +"Hello World"
                            }
                        }
                    }
                }
            }
        }
    }
}