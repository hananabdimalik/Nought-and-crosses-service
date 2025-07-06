package com.example.model

class NoughtAndCrossesRepository {

    val gameBoard = MutableList(9) { GameCell(GamePieces.Unplayed, it) }

    var session = GameSession()
    var newPlayer = Player()

    private var noughtCount = 0
    private var crossCount = 0
    private var currentPlayer = Player()

    private fun alternativeGamePiece(): GamePieces {
        return when {
            noughtCount == crossCount -> {
                crossCount++
                return GamePieces.Nought
            }

            crossCount > noughtCount -> {
                noughtCount++
                return GamePieces.Cross
            }
            else -> GamePieces.Unplayed
        }
    }

    fun updateGameBoard(position: Int, player: Player): List<GameCell> {
        if (currentPlayer.id != player.id) {
            currentPlayer = player
            if (session.hasGameBegan && position in 0 until gameBoard.size) {
                gameBoard[position] = gameBoard[position].copy(alternativeGamePiece(), position)
            }
        }

        return gameBoard
    }

    private val winningCombo = listOf(
        0, 1, 2,
        3, 4, 5,
        6, 7, 8,
        0, 3, 6,
        1, 4, 7,
        2, 5, 8,
        0, 4, 8,
        2, 4, 6
    ).chunked(3)

    fun getGameSession(): GameSession {
        val noughtCells = mutableListOf<Int>()
        val crossCells = mutableListOf<Int>()

        gameBoard.forEachIndexed { index, cell ->
            if (cell.piece == GamePieces.Nought) {
                noughtCells.add(index)
            } else if (cell.piece == GamePieces.Cross) {
                crossCells.add(index)
            }
        }

        winningCombo.forEach {
            if (noughtCells.containsAll(it)) {
                return session.copy(hasGameBegan = false, gameState = GameState.Win, currentPlayer = currentPlayer)
            } else if (crossCells.containsAll(it)) {
                return session.copy(hasGameBegan = false, gameState = GameState.Win, currentPlayer = currentPlayer)
            } else if (crossCells.size + noughtCells.size == 9 && winningCombo[winningCombo.size - 1] == it) {
                return session.copy(hasGameBegan = false, gameState = GameState.Draw)
            }
        }
        return session
    }

    fun resetGame(): List<GameCell> {
        session = session.copy(hasGameBegan = true, gameState = GameState.None)
        currentPlayer = Player()
        noughtCount = 0
        crossCount = 0
        gameBoard.forEachIndexed { index, cell ->
            if (index in 0 until gameBoard.size) {
                gameBoard[index] = gameBoard[index].copy(piece = GamePieces.Unplayed, index)
            }
        }
        return gameBoard
    }

    fun addPlayer(player: Player) {
        if (!player.name.isNullOrEmpty()) {
            if (session.players == null || session.players?.size in 0 until 2) {
                val newPlayersList = mutableListOf<Player>()

                val playersIds = mutableListOf<String?>()
                session.players?.forEach {
                    playersIds.add(it.id)
                }

                newPlayer = newPlayer.copy(player.name, player.id, GamePieces.Nought)

                // add player if id is new aka one device per player
                if (session.players?.isNotEmpty() == true) {
                    playersIds.forEach {
                        if (player.id != it) {
                            newPlayersList.add(newPlayer.copy(gamePiece = GamePieces.Cross))
                            session = session.copy(players = session.players?.plus(newPlayersList))
                        }
                    }
                } else {
                    newPlayersList.add(newPlayer)
                    session = session.copy(players = newPlayersList)
                }
            }

            if (session.players?.size == 2) {
                session = session.copy(hasGameBegan = true, gameState = GameState.None)
            }
        }
    }

    fun restartSession(): GameSession {
        session = GameSession()
        gameBoard.forEachIndexed { index, cell ->
            if(index in 0 until gameBoard.size) {
                gameBoard[index] = gameBoard[index].copy(piece = GamePieces.Unplayed, index)
            }
        }
        return session
    }
}
