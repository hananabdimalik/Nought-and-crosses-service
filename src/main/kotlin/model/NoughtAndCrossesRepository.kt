package com.example.model

class NoughtAndCrossesRepository {

    val gameBoard = MutableList(9) { GameCell(GamePieces.Unplayed, it) }

    fun updateGameBoard(position: Int): List<GameCell> {
        if (position in 0 until gameBoard.size) {
            gameBoard[position] = gameBoard[position].copy(GamePieces.Nought, position)
        }
        return gameBoard
    }
}