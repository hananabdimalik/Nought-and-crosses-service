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
            val position = call.parameters["position"]?.toInt()
            if (position != null) {
                val updatedBoard = repo.updateGameBoard(position)
                call.respond(HttpStatusCode.OK, updatedBoard)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid position")
            }
        }

        get("/gameState") {
            val gameState = repo.getGameState(repo.gameBoard)
            call.respond(HttpStatusCode.OK, gameState)
        }

        get("/resetGame") {
            val newGameBoard = repo.resetGame()
            call.respond(HttpStatusCode.OK, newGameBoard)
        }
    }
}
