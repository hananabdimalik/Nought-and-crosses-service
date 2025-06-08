package com.example.model

class NoughtAndCrossesRepository {

    val gameBoard = MutableList(9) { GameCell(GamePieces.Unplayed, it) }

    var gameSession = GameSession()

    private var noughtCount = 0
    private var crossCount = 0

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

    fun updateGameBoard(position: Int): List<GameCell> {
        if (gameSession.hasGameBegan && position in 0 until gameBoard.size) {
            gameBoard[position] = gameBoard[position].copy(alternativeGamePiece(), position)
        }
        return gameBoard
    }

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

    fun getGameState(list: List<GameCell>): GameState {
        val noughtCells = mutableListOf<Int>()
        val crossCells = mutableListOf<Int>()

        list.forEachIndexed { index, cell ->
            if (cell.piece == GamePieces.Nought) {
                noughtCells.add(index)
            } else if (cell.piece == GamePieces.Cross) {
                crossCells.add(index)
            }
        }

        winningCombo.forEach {
            if (noughtCells.containsAll(it)) {
                return GameState.Win
            } else if (crossCells.containsAll(it)) {
                return GameState.Win
            } else if (crossCells.size + noughtCells.size == 9 && winningCombo[winningCombo.size - 1] == it) {
                return GameState.Draw
            }
        }
        return GameState.None
    }

    fun resetGame(): List<GameCell> {
        gameSession = gameSession.copy(hasGameBegan = true, gameState = GameState.None)
        noughtCount = 0
        crossCount = 0
        gameBoard.forEachIndexed { index, cell ->
            if (index in 0 until gameBoard.size) {
                gameBoard[index] = gameBoard[index].copy(piece = GamePieces.Unplayed, index)
            }
        }
        return gameBoard
    }

    fun addPlayer(player: String) {
        if (player.isNotEmpty()) {
            if (gameSession.players.size in 0 until 2) {
                gameSession = gameSession.copy(players = gameSession.players + player)
            }
            if (gameSession.players.size == 2) {
                gameSession = gameSession.copy(hasGameBegan = true, gameState = GameState.None)
            }
        }
    }
}
