package com.example

import com.example.model.GameSessionState
import com.example.repository.NoughtAndCrossesRepository
import com.example.model.Player
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repo: NoughtAndCrossesRepository) {
    val gameSessionManager = repo.sessionManager
    routing {
        post("/hostSession") {
            val player = call.receive<Player>()
            gameSessionManager.hostSession(player)
            call.respond(HttpStatusCode.OK, gameSessionManager.gameSession)
        }

        post ("/joinSession"){
            val player = call.receive<Player>()
            gameSessionManager.joinGameSession(player)
            if (gameSessionManager.gameSession.gameSessionState == GameSessionState.Started) {
                call.respond(HttpStatusCode.OK, gameSessionManager.gameSession)
            } else {
                call.respond(HttpStatusCode.NotFound, gameSessionManager.gameSession)
            }
        }

        get("/loadGameSession") {
            call.respond(HttpStatusCode.OK, gameSessionManager.gameSession)
        }

        get("/gameBoard") {
            call.respond(HttpStatusCode.OK, repo.gameBoard)
        }

        post("/updateBoard/{position}/{sessionId}") {
            val position = call.parameters["position"]?.toIntOrNull() // if just toInt -> unhandled exception crashes the handler, and Ktor returns a 500
            val sessionId = call.parameters["sessionId"]

            val player = call.receive<Player>()
            if (position != null) {
                val updatedBoard = repo.updateGameBoard(position, player, sessionId)
                call.respond(HttpStatusCode.OK, updatedBoard)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid position")
            }
        }

        get("/getGameState") {
            call.respond(HttpStatusCode.OK, repo.getGameSession())
        }

        get("/resetGame") {
            val newGameBoard = repo.resetGame()
            call.respond(HttpStatusCode.OK, newGameBoard)
        }

        get("/restartGameSession") {
            val gameSessionId = call.receive<String>()
            val newSession = repo.restartGame(gameSessionId)
            call.respond(HttpStatusCode.OK, newSession)
        }
    }
}
