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
data class GameSession(
    val players: List<Player>? = emptyList(),
    val gameState: GameState = GameState.None,
    val currentPlayer: Player? = null,
    var gameSessionState: GameSessionState = GameSessionState.Ended
)

@Serializable
data class Player(
    val name: String? = null,
    val id: String? = null,
    val gamePiece: GamePieces = GamePieces.Unplayed
)

@Serializable
enum class GameSessionState {
    Waiting, Started, Ended
}
