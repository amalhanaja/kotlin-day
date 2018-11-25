package io.github.amalhanaja.db

import io.github.amalhanaja.notes.NoteTable
import io.ktor.application.Application
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DBConfig {

    @Volatile
    var db: Database? = null
        private set

    @KtorExperimentalAPI
    fun init(app: Application) {
        val dbEnv = app.environment.config.config("database")
        db = Database.connect(
            url = dbEnv.property("url").getString(),
            driver = dbEnv.property("driver").getString(),
            user = dbEnv.propertyOrNull("user")?.getString().orEmpty(),
            password = dbEnv.propertyOrNull("password")?.getString().orEmpty()
        ).also {
            transaction(it) {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(NoteTable)
            }
        }
    }
}