package model

import com.example.model.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GameSessionManagerTest {

    private val sut = GameSessionManager(idGenerator = TestIdGenerator())

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
        sut.restartSession()
        assertEquals(GameSession(), sut.gameSession)
    }
}

class TestIdGenerator() : IdGenerator {
    override fun gameSessionId() = "testId"
}
