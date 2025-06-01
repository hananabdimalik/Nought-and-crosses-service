package com.example

import com.example.model.NoughtAndCrossesRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val repo = NoughtAndCrossesRepository()
    routing {
        get("/gameBoard") {
            call.respond(HttpStatusCode.OK, repo.gameBoard)
        }

        get("updateBoard/{position}") {
            val position = call.parameters["position"]?.toInt() ?: 0
            val updatedBoard = repo.updateGameBoard(position)
            call.respond(HttpStatusCode.OK, updatedBoard)
        }

        get("/gameState") {
            val gameState = repo.getGameState()
            call.respond(HttpStatusCode.OK, gameState)
        }
    }
}
