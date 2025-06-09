package fr.uge.wordrawid.screens.solo

val boardActions = List(25) { index ->
    when (index) {
        3, 15 -> CaseAction.MoveForward2
        5, 10 -> CaseAction.MoveBackward3
        8, 18 -> CaseAction.CompassMiniGame
        12, 1 -> CaseAction.RevealTile
        9, 20 -> CaseAction.BalloonMiniGame
        else -> CaseAction.Nothing
    }
}