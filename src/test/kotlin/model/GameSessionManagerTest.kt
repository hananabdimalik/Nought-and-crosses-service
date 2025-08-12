package model

import com.example.model.*
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class GameSessionManagerTest {

    private val testIdGenerator = mock<IdGenerator>()
    private val sut = GameSessionManager(idGenerator = testIdGenerator)

    @Test
    fun `hostSession, given a player with valid name and id, return gameSession`() {
        val player = Player("player-name", id = "player-id", gamePiece = GamePieces.Nought)
        val expected = GameSession(players = listOf(player), gameSessionState = GameSessionState.Waiting)
        assertEquals(expected, sut.hostSession(player))
    }

    @Test
    fun `hostSession, given a player with invalid name and id, return no gameSession`() {
        val player = Player(name = null, id = null)
        assertEquals(null, sut.hostSession(player))
    }

    @Test
    fun `joinGameSession, if sessionId exist, return gameSession with GameSessionState Started`() {
        val player = Player("player-name", id = "player-id", gamePiece = GamePieces.Cross)
        val gameSession = GameSession(players = listOf(Player("name", id = "id")))
        sut.sessions.put("session-id", gameSession)
        sut.gameSession = gameSession
        sut.joinGameSession(player)
        assertEquals(
            expected = GameSession(
                players = listOf(Player("name", id = "id"), player),
                gameSessionState = GameSessionState.Started
            ), actual = sut.gameSession
        )
    }

    @Test
    fun `joinGameSession, if sessionId does not exist, sessions is empty`() {
        val player = Player("player-name", id = "player-id", gamePiece = GamePieces.Cross)
        sut.joinGameSession(player)
        assertEquals(emptyMap(), sut.sessions)
    }

    @Test
    fun `restartSession, given gameSessionId, new gameSession is created`() {
        sut.restartSession("")
        assertEquals(GameSession(), sut.gameSession)
    }

//    @Test
//    fun `restartSession, when restartSession is called, gameSession is updated with new states`() {
//        sut.restartSession("")
//
//        assertEquals(GameSession(), sut.gameSession)
//        val expected = List(9) { GameCell(GamePieces.Unplayed, it) }
//        assertEquals(expected, sut.gameBoard)
//    }

//    @Test
//    fun `hostSession, given hostSession is called, gameSessionState is updated to Waiting`() {
//        val player1 = Player("Dylan", "id", gamePiece = GamePieces.Nought)
//        assertEquals(GameSessionState.Waiting, gameSessionManager.hostSession(player1)?.gameSessionState)
//    }
//
//    @Test
//    fun `joinGameSession, when joinGameSession is called, gameSessionState is updated to Started`() {
//        val player1 = Player("Bob", "id")
//        gameSessionManager.hostSession(player1)
//        val player2 = Player("Bobby", "id-some")
//        gameSessionManager.joinGameSession(player2)
//        assertEquals(
//            GameSession(gameSessionState = GameSessionState.Started).gameSessionState,
//            gameSessionManager.gameSession.gameSessionState
//        )
//    }
//
//    @Test
//    fun `If player name is empty, players is not updated`() {
//        gameSessionManager.joinGameSession(Player(""))
//        assertTrue(gameSessionManager.gameSession.players?.isEmpty() == true)
//    }
//
//    @Test
//    fun `If methods is called more than 2 times, the players list is not updated`() {
//        gameSessionManager.hostSession(Player("Bob", "id"))
//        gameSessionManager.joinGameSession(Player("Dylan", "newId"))
//        assertTrue(gameSessionManager.gameSession.players?.size == 2)
//
//        gameSessionManager.joinGameSession(Player("Mitch", "otherId"))
//        assertTrue(gameSessionManager.gameSession.players?.size == 2)
//    }
//
//    @Test
//    fun `If 2 players have the same id, the players list size in 1`() {
//        gameSessionManager.hostSession(Player("Bob", "id"))
//        gameSessionManager.joinGameSession(Player("Dylan", "id"))
//
//        assertEquals(1, gameSessionManager.gameSession.players?.size)
//    }

}

