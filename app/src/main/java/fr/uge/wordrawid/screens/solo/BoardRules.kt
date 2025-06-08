package fr.uge.wordrawid.screens.solo

val boardActions = List(25) { index ->
    when (index) {
        3, 15 -> CaseAction.MoveForward2
        5, 10 -> CaseAction.MoveBackward2
        8, 18 -> CaseAction.MiniGame
        else -> CaseAction.Nothing
    }
}