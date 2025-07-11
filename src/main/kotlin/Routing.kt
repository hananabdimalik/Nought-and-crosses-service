package com.example

import com.example.model.NoughtAndCrossesRepository
import com.example.model.Player
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repo: NoughtAndCrossesRepository) {
    routing {
        post("/hostSession") {
            val player = call.receive<Player>()
            repo.hostSession(player)
            call.respond(HttpStatusCode.OK, repo.session)
        }

        post ("/join"){
            val player = call.receive<Player>()
            repo.joinGameSession(player)
            call.respond(HttpStatusCode.OK, repo.session.gameSessionState)
        }

        get("/gameBoard") {
            call.respond(HttpStatusCode.OK, repo.gameBoard)
        }

        post("/updateBoard/{position}") {
            val position = call.parameters["position"]?.toIntOrNull() // if just toInt -> unhandled exception crashes the handler, and Ktor returns a 500
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

        get("/restartGameSession") {
            val newGameSession = repo.restartSession()
            call.respond(HttpStatusCode.OK, newGameSession)
        }
    }
}
