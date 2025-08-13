package model

import com.example.model.*
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun `If player name is empty, players is not updated`() {
        sut.joinGameSession(Player(""))
        assertTrue(sut.gameSession.players?.isEmpty() == true)
    }

    @Test
    fun `If methods is called more than 2 times, the players list is not updated`() {
        sut.hostSession(Player("Bob", "id"))
        sut.joinGameSession(Player("Dylan", "newId"))
        assertTrue(sut.gameSession.players?.size == 2)

        sut.joinGameSession(Player("Mitch", "otherId"))
        assertTrue(sut.gameSession.players?.size == 2)
    }

    @Test
    fun `If 2 players have the same id, the players list size in 1`() {
        sut.hostSession(Player("Bob", "id"))
        sut.joinGameSession(Player("Dylan", "id"))

        assertEquals(1, sut.gameSession.players?.size)
    }

}

