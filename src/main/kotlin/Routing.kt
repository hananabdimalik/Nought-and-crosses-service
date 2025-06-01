package com.example

import com.example.model.NoughtAndCrossesRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/gameBoard") {
            val repo = NoughtAndCrossesRepository()
            call.respond(HttpStatusCode.OK, repo.gameBoard)
        }
    }
}
