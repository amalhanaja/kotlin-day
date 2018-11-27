package io.github.amalhanja.notes

import io.github.amalhanja.http.HttpMethod
import io.github.amalhanja.http.http
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.parseList
import kotlinx.serialization.stringify
import react.*
import react.dom.*
import kotlin.coroutines.CoroutineContext
import kotlin.js.Date

@ImplicitReflectionSerializer
class NotesPage(props: RProps) : RComponent<RProps, NotePageState>(props = props), CoroutineScope {
    init {
        state = NotePageState()
        getAllNotes()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    override fun RBuilder.render() {
        div {
            if (state.note != null) dialogElement(state.note!!)
            noteList(
                state.notes,
                onItemDeleteClicked = { pos -> setState { deleteNote(notes[pos], pos) } },
                onItemEditClicked = { pos -> setState { note = notes[pos] } }
            )

            button(classes = "circular ui icon button") {
                attrs {
                    onClickFunction = { event ->
                        setState { note = Note() }
                    }
                    jsStyle {
                        position = "fixed"
                        bottom = "0px"
                        right = "0px"
                        width = "48px"
                        height = "48px"
                        marginBottom = "16px"
                        marginRight = "16px"
                    }
                }
                i(classes = "icon add") {}
            }
        }
    }

    private fun RBuilder.dialogElement(note: Note) {
        div(classes = "ui dimmer modals page top aligned visible active") {
            div(classes = "ui special modal scrolling visible active") {
                div("header") {
                    +"Add New Note"
                }
                div(classes = "content") {
                    form(classes = "ui form") {
                        div(classes = "field") {
                            label { +"Title" }
                            input(type = InputType.text) {
                                attrs.placeholder = "Some Title..."
                                attrs.onChangeFunction = {
                                    state.note = state.note?.copy(title = it.target.asDynamic().value)
                                }
                                attrs.defaultValue = note.title
                            }
                        }
                        div(classes = "field") {
                            label { +"Content" }
                            textArea {
                                attrs.defaultValue = note.body
                                attrs.placeholder = "Ho-ho-ho! beauty of courage."
                                attrs.onChangeFunction = {
                                    state.note = state.note?.copy(body = it.target.asDynamic().value)
                                }
                            }
                        }
                    }
                }
                div(classes = "actions") {
                    div(classes = "ui negative button") {
                        +"Cancel"
                        attrs.onClickFunction = { setState { this.note = null } }
                    }
                    div(classes = "ui positive right labeled icon button") {
                        attrs.onClickFunction = { submitNote() }
                        +"Submit"
                        i(classes = "checkmark icon") {}
                    }
                }
            }
        }
    }

    private fun submitNote() {
        state.note?.run note@{
            if (id.isEmpty()) {
                setState { notes.add(0, this@note.copy(date = Date().toUTCString())) }
                return@note createNote(this@note)
            } else {
                val note: Note = state.notes.first { it.id == this@note.id }
                return@note updateNote(note, this@note)
            }
        }
        setState { note = null }
    }

    private fun deleteNote(note: Note, pos: Int) = launch {
        setState { notes.removeAt(pos) }
        runCatching {
            http(
                HttpMethod.DELETE,
                url = "http://localhost:9000/notes/${note.id}",
                headers = mapOf("Content-Type" to "application/json")
            )
        }.onFailure { setState { notes.add(pos, note) } }.onSuccess { joinAll() }
    }

    private fun updateNote(from: Note, to: Note) = launch {
        val tmp = from.copy()
        setState {
            from.title = to.title
            from.body = to.body
        }
        runCatching {
            http(
                HttpMethod.PUT,
                url = "http://localhost:9000/notes/${from.id}",
                body = JSON.stringify(to),
                headers = mapOf("Content-Type" to "application/json")
            )
        }.onFailure {
            setState {
                from.title = tmp.title
                from.body = tmp.body
            }
        }
    }

    private fun createNote(newNote: Note) = launch {
        runCatching {
            http(
                HttpMethod.POST,
                url = "http://localhost:9000/notes",
                body = JSON.stringify(newNote),
                headers = mapOf("Content-Type" to "application/json")
            )
        }.onSuccess {
            getAllNotes()
        }.onFailure {
            setState { notes.removeAll { n -> n.id == newNote.id } }
        }
    }

    private fun getAllNotes() = launch {
        runCatching {
            JSON.parseList<Note>(
                http(
                    method = HttpMethod.GET,
                    url = "http://localhost:9000/notes",
                    headers = mapOf("Content-Type" to "application/json")
                )
            ).toMutableList()
        }.onSuccess { setState { notes = it } }.onFailure { console.log(it) }
    }
}

data class NotePageState(
    var isLoading: Boolean = false,
    var notes: MutableList<Note> = mutableListOf(),
    var note: Note? = null
) : RState
