package fr.uge.wordrawid.screens.solo

sealed class CaseAction {
    object MoveForward2 : CaseAction()
    object MoveBackward2 : CaseAction()
    object MiniGame : CaseAction()
    object RevealTile : CaseAction()
    object Nothing : CaseAction()
}
