package com.example.model

class NoughtAndCrossesRepository {

    var gameBoard = MutableList(9) { GameCell(GamePieces.Unplayed, it) }
}