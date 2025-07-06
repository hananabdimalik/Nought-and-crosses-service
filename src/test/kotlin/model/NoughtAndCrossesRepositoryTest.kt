package model

import com.example.model.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoughtAndCrossesRepositoryTest {

    val repo = NoughtAndCrossesRepository(GameSessionManager())

    @BeforeTest
    fun setUp() {
        repo.session = GameSession()
    }

    @Test
    fun `updateGameBoard, given a cell position, gameBoard is updated with GridCell containing a gamePiece at said position`() {
        repo.session = repo.session.copy(gameSessionState = GameSessionState.Started)
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
        repo.session = repo.session.copy(gameSessionState = GameSessionState.Started)

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
        repo.session = repo.session.copy(gameSessionState = GameSessionState.Started)

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
        val actual = repo.getGameSession()
        assertEquals(repo.session.gameState, actual.gameState)
    }

    @Test
    fun `resetGame, when resetGame is called, gameBoard is initialised to unplayed state`() {
        assertEquals(List(9) { GameCell(GamePieces.Unplayed, it) }, repo.resetGame())
    }

    @Test
    fun `joinGameSession, when joinGameSession is called with one name, gameSession player object is updated with name`() {
        repo.joinGameSession(Player("Bob", "id"))
        assertTrue(repo.session.players?.contains(Player("Bob", "id", gamePiece = GamePieces.Nought)) == true)
    }

    @Test
    fun `If player name is empty, players is not updated`() {
        repo.joinGameSession(Player(""))
        assertTrue(repo.session.players?.isEmpty() == true)
    }

    @Test
    fun `If methods is called more than 2 times, the players list is not updated`() {
        repo.hostSession(Player("Bob", "id"))
        repo.joinGameSession(Player("Dylan", "newId"))
        assertTrue(repo.session.players?.size == 2)

        repo.joinGameSession(Player("Mitch", "otherId"))
        assertTrue(repo.session.players?.size == 2)
    }

    @Test
    fun `If 2 players have the same id, the players list size in 1`() {
        repo.hostSession(Player("Bob", "id"))
        repo.joinGameSession(Player("Dylan", "id"))

        assertEquals(1, repo.session.players?.size)
    }

    @Test
    fun `getGameSession, given a winningCombo, return winning player`() {

        // add player
        val player1 = Player("Dylan", "id", gamePiece = GamePieces.Nought)
        val player2 = Player("Bob", "Bobs-id", gamePiece = GamePieces.Cross)
        repo.joinGameSession(player2)
        repo.session = repo.session.copy(currentPlayer = player1, gameSessionState = GameSessionState.Started)

        // replicate a game
        repo.updateGameBoard(2, player1.copy(gamePiece = GamePieces.Nought))
        repo.session = repo.session.copy(currentPlayer = player2)

        repo.updateGameBoard(3, player2.copy(gamePiece = GamePieces.Cross))
        repo.session = repo.session.copy(currentPlayer = player1)

        repo.updateGameBoard(0, player1.copy(gamePiece = GamePieces.Nought))
        repo.session = repo.session.copy(currentPlayer = player2)

        repo.updateGameBoard(4, player2.copy(gamePiece = GamePieces.Cross))
        repo.session = repo.session.copy(currentPlayer = player1)

        repo.updateGameBoard(6, player1.copy(gamePiece = GamePieces.Nought))
        repo.session = repo.session.copy(currentPlayer = player2)

        repo.updateGameBoard(5, player2.copy(gamePiece = GamePieces.Cross))

        // get game state
        val actual = repo.getGameSession()

        assertEquals(GameState.Win, actual.gameState)
        assertEquals(player2, actual.currentPlayer)
    }

    @Test
    fun `restartSession, when restartSession is called, gameSession is updated with new states`() {
        repo.restartSession()

        assertEquals(GameSession(), repo.session)
        val expected = List(9) { GameCell(GamePieces.Unplayed, it) }
        assertEquals(expected, repo.gameBoard)
    }

    @Test
    fun `hostSession, given hostSession is called, gameSessionState is updated to Waiting`() {
        val player1 = Player("Dylan", "id", gamePiece = GamePieces.Nought)
        assertEquals(GameSessionState.Waiting, repo.hostSession(player1).gameSessionState)
    }

    @Test
    fun `joinGameSession, when joinGameSession is called, gameSessionState is updated to Started`() {
        val player1 = Player("Bob", "id")
        repo.hostSession(player1)
        val player2 = Player("Bobby", "id-some")
        repo.joinGameSession(player2)
        assertEquals(
            GameSession(gameSessionState = GameSessionState.Started).gameSessionState,
            repo.session.gameSessionState
        )
    }
}
