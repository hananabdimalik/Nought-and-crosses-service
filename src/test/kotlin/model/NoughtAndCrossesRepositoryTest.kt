package com.example.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoughtAndCrossesRepositoryTest {

    val repo = NoughtAndCrossesRepository()

    @Test
    fun `updateGameBoard, given a cell position, gameBoard is updated with GridCell containing a gamePiece at said position`() {
        repo.gameSession = repo.gameSession.copy(hasGameBegan = true)
        repo.updateGameBoard(2, Player("Bob", "Bob-Id"))
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
    fun `updateGameBoard, given a cell position when updateGameBoard is called twice by the same player, gameBoard is not updated the second time`() {
        repo.gameSession = repo.gameSession.copy(hasGameBegan = true)

        repo.updateGameBoard(2, Player("Bob", "Bob-Id"))
        repo.updateGameBoard(3, Player("Bob", "Bob-Id"))
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
    fun `updateGameBoard, given a cell position when updateGameBoard is called twice by the different player, gameBoard is  updated each time`() {
        repo.gameSession = repo.gameSession.copy(hasGameBegan = true)

        repo.updateGameBoard(2, Player("Bob", "Bob-Id"))
        repo.updateGameBoard(3, Player("Dylan", "Dylan-Id"))

        val expected = listOf(
            GameCell(GamePieces.Unplayed, 0),
            GameCell(GamePieces.Unplayed, 1),
            GameCell(GamePieces.Nought, 2),
            GameCell(GamePieces.Cross, 3),
            GameCell(GamePieces.Unplayed, 4),
            GameCell(GamePieces.Unplayed, 5),
            GameCell(GamePieces.Unplayed, 6),
            GameCell(GamePieces.Unplayed, 7),
            GameCell(GamePieces.Unplayed, 8),
        )
        assertEquals(expected, repo.gameBoard)
    }

    @Test
    fun `getGameSession, when theres no winningCombo in board, gameState is None`() {

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
        val actual = repo.getGameSession(input)
        assertEquals(repo.gameSession.gameState, actual.gameState)
    }

    @Test
    fun `resetGame, when resetGame is called, gameBoard is initialised to unplayed state`() {
        assertEquals(List(9) { GameCell(GamePieces.Unplayed, it) }, repo.resetGame())
    }

    @Test
    fun `addPlayer, when addPlayer is called with one name, gameSession player object is updated with name`() {
        repo.addPlayer(Player("Bob", "id"))
        assertTrue(repo.gameSession.players?.contains(Player("Bob", "id")) == true)
    }

    @Test
    fun `addPlayer, if player name is empty, players is not updated`() {
        repo.addPlayer(Player(""))
        assertTrue(repo.gameSession.players?.isEmpty() == true)
    }

    @Test
    fun `addPlayer, if methods is called more than 2 times, the players list is not updated`(){
        repo.addPlayer(Player("Bob", "id"))
        repo.addPlayer(Player("Dylan", "newId"))
        assertTrue(repo.gameSession.players?.size == 2)

        repo.addPlayer(Player("Mitch", "otherId"))
        assertTrue(repo.gameSession.players?.size == 2)
    }

    @Test
    fun `addPlayer, if 2 players have the same id, the players list size in 1`() {
        repo.addPlayer(Player("Bob", "id"))
        repo.addPlayer(Player("Dylan", "id"))

        assertEquals(1, repo.gameSession.players?.size)
    }

    @Test
    fun `getGameSession, given a winningCombo, return winning player`() {

        // add player
        val player = Player("Dylan", "id")
        repo.addPlayer(player)

        // set currentPlayer
        repo.updateGameBoard(3, player)

        val input = listOf(
            GameCell(GamePieces.Unplayed, 0),
            GameCell(GamePieces.Unplayed, 1),
            GameCell(GamePieces.Nought, 2),
            GameCell(GamePieces.Cross, 3),
            GameCell(GamePieces.Cross, 4),
            GameCell(GamePieces.Cross, 5),
            GameCell(GamePieces.Nought, 6),
            GameCell(GamePieces.Unplayed, 7),
            GameCell(GamePieces.Unplayed, 8),
        )

        // get game state
        val actual = repo.getGameSession(input)

        assertEquals(GameState.Win, actual.gameState)
        assertEquals(player, actual.currentPlayer)
    }
}
