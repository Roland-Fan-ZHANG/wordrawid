package fr.uge.wordrawid.screens.solo

val boardActions = List(25) { index ->
    when (index) {
        3, 13, 15 -> CaseAction.MoveForward2
        5, 7, 16, 10 -> CaseAction.MoveBackward3
        8, 6, 18 -> CaseAction.CompassMiniGame
        12, 4, 19, 1 -> CaseAction.RevealTile
        9, 2, 20 -> CaseAction.BalloonMiniGame
        else -> CaseAction.Nothing
    }
}