package com.example.repository

import com.example.model.GameCell
import com.example.model.GamePieces
import com.example.model.GameSession
import com.example.GameSessionManager
import com.example.model.GameSessionState
import com.example.model.GameState
import com.example.model.Player
import com.example.model.RestartGame

class NoughtAndCrossesRepository(val sessionManager: GameSessionManager) {
    private val session get() = sessionManager.gameSession // get here allows us to reference current session instead of storing it
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

    fun updateGameBoard(position: Int, player: Player, sessionId: String?): List<GameCell> {
        if (currentPlayer.id != player.id && sessionManager.gameSession.sessionId == sessionId) {
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
            session.copy(gameState = GameState.None)
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

    fun restartGame(gameSessionId: String): RestartGame {
        gameBoard.forEachIndexed { index, _ ->
            gameBoard[index] = gameBoard[index].copy(GamePieces.Unplayed, index)
        }
        sessionManager.sessions.remove(gameSessionId)
        sessionManager.gameSession = GameSession()
        return RestartGame(sessionManager.gameSession, gameBoard)
    }
}