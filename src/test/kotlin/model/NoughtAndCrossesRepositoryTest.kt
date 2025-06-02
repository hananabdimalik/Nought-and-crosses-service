package com.example.model

import kotlin.test.Test
import kotlin.test.assertEquals

class NoughtAndCrossesRepositoryTest {

    val repo = NoughtAndCrossesRepository()

    @Test
    fun `updateGameBoard, when cell position, gameBoard is updated with GridCell containing a gamePiece at said position`() {
        repo.updateGameBoard(2)
        val expected = listOf(
            GameCell(GamePieces.Unplayed, 0),
            GameCell(GamePieces.Unplayed, 1),
            GameCell(GamePieces.Nought, 2),
            GameCell(GamePieces.Unplayed, 3),
            GameCell(GamePieces.Unplayed, 4),
            GameCell(GamePieces.Unplayed, 5),
            GameCell(GamePieces.Unplayed, 6),
            GameCell(GamePieces.Unplayed, 7),
            GameCell(GamePieces.Unplayed, 8),
        )
        assertEquals(expected, repo.gameBoard)
    }

    @Test
    fun `getGameState, when theres no winningCombo in board, gameState is None`() {

        val input = listOf(
            GameCell(GamePieces.Unplayed, 0),
            GameCell(GamePieces.Unplayed, 1),
            GameCell(GamePieces.Nought, 2),
            GameCell(GamePieces.Cross, 3),
            GameCell(GamePieces.Nought, 4),
            GameCell(GamePieces.Cross, 5),
            GameCell(GamePieces.Cross, 6),
            GameCell(GamePieces.Unplayed, 7),
            GameCell(GamePieces.Unplayed, 8),
        )
        val actual = repo.getGameState(input)
        assertEquals(GameState.None, actual)
    }

    @Test
    fun `resetGame, when resetGame is called, gameBoard is initialised to unplayed state`() {
        assertEquals(List(9) { GameCell(GamePieces.Unplayed, it) }, repo.resetGame())
    }
}
