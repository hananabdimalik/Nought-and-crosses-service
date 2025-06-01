package com.example.model

class NoughtAndCrossesRepository {

    val gameBoard = MutableList(9) { GameCell(GamePieces.Unplayed, it) }

    fun updateGameBoard(position: Int): List<GameCell> {
        if (position in 0 until gameBoard.size) {
            gameBoard[position] = gameBoard[position].copy(GamePieces.Nought, position)
        }
        return gameBoard
    }

    val winningCombo = listOf(
        0, 1, 2,
        3, 4, 5,
        6, 7, 8,
        0, 3, 6,
        1, 4, 7,
        2, 5, 8,
        0, 4, 8,
        2, 4, 6
    ).chunked(3)

    fun getGameState(): GameState {
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
                return GameState.Win
            } else if (crossCells.containsAll(it)) {
                return GameState.Win
            } else if (crossCells.size + noughtCells.size == 9 && winningCombo[winningCombo.size - 1] == it) {
                return GameState.Draw
            }
        }
        return GameState.None
    }
}
