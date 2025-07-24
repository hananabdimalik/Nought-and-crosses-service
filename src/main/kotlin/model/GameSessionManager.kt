package com.example.model

class GameSessionManager(
    val sessions: MutableMap<String, GameSession> = mutableMapOf(),
    idGenerator: IdGenerator
) {

    var gameSession: GameSession = GameSession()
    private val gameSessionId = idGenerator.gameSessionId()
    private var newPlayer = Player()

    fun hostSession(player: Player): GameSession? {
        sessions[gameSessionId] = gameSession
        addPlayer(player, gameSession)
        return if (gameSession.players?.isNotEmpty() == true) {
            gameSession = gameSession.copy(gameSessionState = GameSessionState.Waiting, sessionId = gameSession.sessionId)
            gameSession
        } else null
    }

    fun joinGameSession(player: Player) {
        addPlayer(player, gameSession)
        gameSession = gameSession.copy(players = gameSession.players)
        if (gameSession.players?.size == 2) {
            gameSession = gameSession.copy(gameSessionState = GameSessionState.Started)
        }
    }

    private fun addPlayer(player: Player, session: GameSession?) {
        if (session == null) {
            return
        }

        if (isPlayerValid(player) && session.players == null || session.players?.size in 0 until 2) {
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
                if (isPlayerValid(newPlayer)) {
                    newPlayersList.add(newPlayer)
                    gameSession = session.copy(players = newPlayersList)
                }
            }
        }
    }

    private fun isPlayerValid(player: Player) =
        !player.name.isNullOrEmpty() && !player.id.isNullOrEmpty()

    fun restartSession(): GameSession {
        sessions.remove(gameSessionId)
        gameSession = GameSession()
        return gameSession
    }
}
