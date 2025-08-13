package model

import com.example.model.*
import kotlin.test.Test
import kotlin.test.assertEquals

class NoughtAndCrossesRepositoryTest {

    private val idGeneratorImpl = IdGeneratorImpl()
    private val gameSessionManager = GameSessionManager( idGenerator = idGeneratorImpl)
    private val repo = NoughtAndCrossesRepository(GameSessionManager(idGenerator = idGeneratorImpl))

    @Test
    fun `updateGameBoard, given a cell position, gameBoard is updated with GridCell containing a gamePiece at said position`() {
        repo.sessionManager.gameSession =
            gameSessionManager.gameSession.copy(gameSessionState = GameSessionState.Started, sessionId = "some-sessionId")
        repo.updateGameBoard(2, Player("Bob", "Bob-Id"), "some-sessionId")
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
        repo.sessionManager.gameSession =
            gameSessionManager.gameSession.copy(gameSessionState = GameSessionState.Started, sessionId = "some-sessionId")

        repo.updateGameBoard(2, Player("Bob", "Bob-Id"), sessionId = "some-sessionId")
        repo.updateGameBoard(3, Player("Bob", "Bob-Id"), sessionId = "some-sessionId")
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
        repo.sessionManager.gameSession =
            gameSessionManager.gameSession.copy(gameSessionState = GameSessionState.Started, sessionId = "some-sessionId")

        repo.updateGameBoard(2, Player("Bob", "Bob-Id"), sessionId = "some-sessionId")
        repo.updateGameBoard(3, Player("Dylan", "Dylan-Id"), sessionId = "some-sessionId")

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
        assertEquals(gameSessionManager.gameSession.gameState, actual.gameState)
    }

    @Test
    fun `resetGame, when resetGame is called, gameBoard is initialised to unplayed state`() {
        assertEquals(List(9) { GameCell(GamePieces.Unplayed, it) }, repo.resetGame())
    }

    @Test
    fun `getGameSession, given a winningCombo, return winning player`() {

        // add player
        val player1 = Player("Dylan", "id", gamePiece = GamePieces.Nought)
        val player2 = Player("Bob", "Bobs-id", gamePiece = GamePieces.Cross)
        gameSessionManager.joinGameSession(player2)
        repo.sessionManager.gameSession =
            gameSessionManager.gameSession.copy(currentPlayer = player1, gameSessionState = GameSessionState.Started, sessionId = "some-sessionId")

        // replicate a game
        repo.updateGameBoard(2, player1.copy(gamePiece = GamePieces.Nought), sessionId = "some-sessionId")
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player2)

        repo.updateGameBoard(3, player2.copy(gamePiece = GamePieces.Cross), sessionId = "some-sessionId")
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player1)

        repo.updateGameBoard(0, player1.copy(gamePiece = GamePieces.Nought), sessionId = "some-sessionId")
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player2)

        repo.updateGameBoard(4, player2.copy(gamePiece = GamePieces.Cross), sessionId = "some-sessionId")
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player1)

        repo.updateGameBoard(6, player1.copy(gamePiece = GamePieces.Nought), sessionId = "some-sessionId")
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player2)

        repo.updateGameBoard(5, player2.copy(gamePiece = GamePieces.Cross), sessionId = "some-sessionId")

        // get game state
        val actual = repo.getGameSession()

        assertEquals(GameState.Win, actual.gameState)
        assertEquals(player2, actual.currentPlayer)
    }

    @Test
    fun `restartGame, given gameSessionId, new gameSession is created with empty board`() {
        repo.restartGame("sessionId")
        assertEquals(GameSession(), repo.sessionManager.gameSession)
        assertEquals(List(9) { GameCell(GamePieces.Unplayed, it) }, repo.gameBoard)
    }
}
