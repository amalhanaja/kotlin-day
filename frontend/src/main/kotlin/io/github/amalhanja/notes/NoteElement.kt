package io.github.amalhanja.notes

import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.ReactElement
import react.dom.*
import kotlin.js.Date

fun RBuilder.noteItem(note: Note, onDeleteClicked: () -> Unit, onEditClicked: () -> Unit): ReactElement? =
    div(classes = "ui cards") {
        div(classes = "card") {
            div(classes = "content") {
                div(classes = "header") {
                    +note.title
                    i(classes = "right floated trash icon") {
                        attrs {
                            jsStyle {
                                cursor = "pointer"
                                color = "red"
                            }
                            onClickFunction = { onDeleteClicked.invoke() }
                        }
                    }
                }
                div(classes = "meta") {
                    span {
                        val date = Date(note.date)
                        +"${date.toDateString()} "
                        b { +"(${date.getHours()}:${date.getMinutes()})" }
                    }
                }
            }
            div(classes = "content") {
                div(classes = "description") { +note.body }
            }
            div(classes = "ui bottom attached primary button") {
                attrs.onClickFunction = { onEditClicked.invoke() }
                i(classes = "edit icon") {}
                +"Edit"
            }
        }
    }

fun RBuilder.noteList(
    notes: List<Note>,
    onItemEditClicked: (pos: Int) -> Unit,
    onItemDeleteClicked: (pos: Int) -> Unit
) {
    div(classes = "ui grid container") {
        div(classes = "ui four column doubling grid") {
            notes.mapIndexed { i, n ->
                div(classes = "column") {
                    noteItem(n, onDeleteClicked = {
                        onItemDeleteClicked.invoke(i)
                    }, onEditClicked = {
                        onItemEditClicked.invoke(i)
                    })
                }
            }
        }
    }
}