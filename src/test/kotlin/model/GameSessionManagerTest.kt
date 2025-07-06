package model

import com.example.model.GameSession
import com.example.model.GameSessionManager
import com.example.model.GameSessionState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameSessionManagerTest {

    val sut = GameSessionManager()

    @Test
    fun `createGameSession, given a sessionId, new game session is created`() {
        assertEquals(
            GameSession().copy(gameSessionState = GameSessionState.Waiting),
            sut.createGameSession("sessionId")
        )
    }

    @Test
    fun `getGameSession, given a sessionId, return gameSession`() {
        val newGameSession = sut.createGameSession("sessionId")
        assertEquals(newGameSession, sut.getGameSession("sessionId"))
    }

    @Test
    fun `removeGameSession, given sessionId, remove session from list of sessions`() {
        sut.createGameSession("sessionId")
        sut.removeGameSession("sessionId")
        assertTrue(sut.sessions.isEmpty())
    }

    @Test
    fun `createGameSession, when 2 sessions are created, there're 2 game sessions`() {
        sut.createGameSession("sessionId")
        sut.createGameSession("sessionId 2")
        assertEquals(sut.sessions.size, 2)
    }
}
