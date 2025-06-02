package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class GamePieces {
    Nought, Cross, Unplayed
}

@Serializable
data class GameCell(val piece: GamePieces, val position: Int)

@Serializable
enum class GameState{Win, Draw, None}

@Serializable
data class GameSession(val players: List<String> = emptyList(), val hasGameBegan: Boolean = false, val gameState: GameState = GameState.None)
