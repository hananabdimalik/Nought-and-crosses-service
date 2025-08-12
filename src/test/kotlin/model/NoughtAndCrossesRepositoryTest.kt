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
            gameSessionManager.gameSession.copy(gameSessionState = GameSessionState.Started)
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
        repo.sessionManager.gameSession =
            gameSessionManager.gameSession.copy(gameSessionState = GameSessionState.Started)

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
        repo.sessionManager.gameSession =
            gameSessionManager.gameSession.copy(gameSessionState = GameSessionState.Started)

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
            gameSessionManager.gameSession.copy(currentPlayer = player1, gameSessionState = GameSessionState.Started)

        // replicate a game
        repo.updateGameBoard(2, player1.copy(gamePiece = GamePieces.Nought))
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player2)

        repo.updateGameBoard(3, player2.copy(gamePiece = GamePieces.Cross))
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player1)

        repo.updateGameBoard(0, player1.copy(gamePiece = GamePieces.Nought))
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player2)

        repo.updateGameBoard(4, player2.copy(gamePiece = GamePieces.Cross))
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player1)

        repo.updateGameBoard(6, player1.copy(gamePiece = GamePieces.Nought))
        gameSessionManager.gameSession = gameSessionManager.gameSession.copy(currentPlayer = player2)

        repo.updateGameBoard(5, player2.copy(gamePiece = GamePieces.Cross))

        // get game state
        val actual = repo.getGameSession()

        assertEquals(GameState.Win, actual.gameState)
        assertEquals(player2, actual.currentPlayer)
    }
}
