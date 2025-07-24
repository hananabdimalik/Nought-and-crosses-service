package com.example.model

class NoughtAndCrossesRepository(val sessionManager: GameSessionManager) {
    private val session get() = sessionManager.gameSession // get here allows us to reference currentSession instead of storing it
    val gameBoard = MutableList(9) { GameCell(GamePieces.Unplayed, it) }

    private var noughtCount = 0
    private var crossCount = 0
    private var currentPlayer = Player()
    private val winningCombo = listOf(
        0, 1, 2,
        3, 4, 5,
        6, 7, 8,
        0, 3, 6,
        1, 4, 7,
        2, 5, 8,
        0, 4, 8,
        2, 4, 6
    ).chunked(3)

    fun updateGameBoard(position: Int, player: Player): List<GameCell> {
        if (currentPlayer.id != player.id) {
            currentPlayer = player
            if (session.gameSessionState == GameSessionState.Started && position in 0 until gameBoard.size) {
                gameBoard[position] = gameBoard[position].copy(alternativeGamePiece(), position)
            }
        }

        return gameBoard
    }

    fun getGameSession(): GameSession {
        val noughtCells = mutableListOf<Int>()
        val crossCells = mutableListOf<Int>()

        gameBoard.forEachIndexed { index, cell ->
            if (cell.piece == GamePieces.Nought) {
                noughtCells.add(index)
            } else if (cell.piece == GamePieces.Cross) {
                crossCells.add(index)
            }
        }

        winningCombo.forEach {
            if (noughtCells.containsAll(it)) {
                return session.copy(
                    gameSessionState = GameSessionState.Ended,
                    gameState = GameState.Win,
                    currentPlayer = currentPlayer
                )
            } else if (crossCells.containsAll(it)) {
                return session.copy(
                    gameSessionState = GameSessionState.Ended,
                    gameState = GameState.Win,
                    currentPlayer = currentPlayer
                )
            } else if (crossCells.size + noughtCells.size == 9 && winningCombo[winningCombo.size - 1] == it) {
                return session.copy(gameSessionState = GameSessionState.Ended, gameState = GameState.Draw)
            }
        }
        return session
    }

    fun resetGame(): List<GameCell> {
        sessionManager.gameSession =
            session.copy(gameSessionState = GameSessionState.Ended, gameState = GameState.None)
        currentPlayer = Player()
        noughtCount = 0
        crossCount = 0
        gameBoard.forEachIndexed { index, _ ->
            if (index in 0 until gameBoard.size) {
                gameBoard[index] = gameBoard[index].copy(piece = GamePieces.Unplayed, index)
            }
        }
        return gameBoard
    }

    private fun alternativeGamePiece(): GamePieces {
        return when {
            noughtCount == crossCount -> {
                crossCount++
                return GamePieces.Nought
            }

            crossCount > noughtCount -> {
                noughtCount++
                return GamePieces.Cross
            }

            else -> GamePieces.Unplayed
        }
    }
}
