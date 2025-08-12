package com.example.model

class GameSessionManager(
    val idGenerator: IdGenerator
) {
    val sessions: MutableMap<String, GameSession> = mutableMapOf()
    var gameSession: GameSession = GameSession()

    // create a new gameSession each time a game is hosted
    fun hostSession(player: Player): GameSession? {
        val gameSessionId = idGenerator.gameSessionId()
        gameSession = GameSession()
        sessions[gameSessionId] = gameSession
        addPlayer(player, gameSession)
        return if (gameSession.players?.isNotEmpty() == true) {
            gameSession = gameSession.copy(gameSessionState = GameSessionState.Waiting, sessionId = gameSessionId)
            gameSession
        } else null
    }

    // Player joins last hosted game
    fun joinGameSession(player: Player) {
        if (sessions.isNotEmpty()) {
            addPlayer(player, gameSession)
            gameSession = gameSession.copy(players = gameSession.players)
            if (gameSession.players?.size == 2) {
                gameSession = gameSession.copy(gameSessionState = GameSessionState.Started)
            }
        } else {
            gameSession = gameSession.copy(error = "Please enter a game session!")
        }
    }

    private fun addPlayer(player: Player, session: GameSession?) {
        if (session == null) {
            return
        }

        if (isPlayerValid(player) && session.players?.size in 0 until 2) {
            var newPlayer = Player()

            val newPlayersList = mutableListOf<Player>()

            val playersIds = mutableListOf<String?>()
            session.players?.forEach {
                playersIds.add(it.id)
            }

            newPlayer = newPlayer.copy(player.name, player.id, GamePieces.Nought)

            // add player if id is new aka one device per player
            if (session.players?.isNotEmpty() == true) {
                playersIds.forEach {
                    if (player.id != it) {
                        newPlayersList.add(newPlayer.copy(gamePiece = GamePieces.Cross))
                        gameSession = session.copy(players = session.players.plus(newPlayersList))
                    }
                }
            } else {
                    newPlayersList.add(newPlayer)
                    gameSession = session.copy(players = newPlayersList)
            }
        }
    }

    private fun isPlayerValid(player: Player) =
        !player.name.isNullOrEmpty() && !player.id.isNullOrEmpty()

    fun restartSession(gameSessionId: String): GameSession {
        sessions.remove(gameSessionId)
        gameSession = GameSession()
        return gameSession
    }
}
