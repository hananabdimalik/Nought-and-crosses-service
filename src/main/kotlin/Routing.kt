package com.example

import com.example.model.NoughtAndCrossesRepository
import com.example.model.Player
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val repo = NoughtAndCrossesRepository()
    routing {
        get("/gameSession"){
            val gameSession = repo.getGameSession(repo.gameBoard)
            call.respond(HttpStatusCode.OK, gameSession)
        }

        post ("/join"){
            val player = call.receive<Player>()
            repo.addPlayer(player)
            call.respond(HttpStatusCode.OK, player)
        }

        get("/gameBoard") {
            call.respond(HttpStatusCode.OK, repo.gameBoard)
        }

        post("updateBoard/{position}") {
            val position = call.parameters["position"]?.toInt()
            val player = call.receive<Player>()
            if (position != null) {
                val updatedBoard = repo.updateGameBoard(position, player)
                call.respond(HttpStatusCode.OK, updatedBoard)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid position")
            }
        }

        get("/resetGame") {
            val newGameBoard = repo.resetGame()
            call.respond(HttpStatusCode.OK, newGameBoard)
        }

        get("/restartGame") {
            val newGameSession = repo.restartSession()
            call.respond(HttpStatusCode.OK, newGameSession)
        }
    }
}
