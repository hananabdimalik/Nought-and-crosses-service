package com.example.model

class GameSessionManager(val sessions: MutableMap<String, GameSession> = mutableMapOf()) {

    fun createGameSession(sessionId: String): GameSession {
        val session = GameSession()
        sessions[sessionId] = session
        sessions[sessionId]?.gameSessionState = GameSessionState.Waiting
        return session
    }

    fun getGameSession(sessionId: String): GameSession {
        return sessions[sessionId] ?: GameSession()
    }

    fun removeGameSession(sessionId: String) {
        sessions.remove(sessionId)
    }
}
