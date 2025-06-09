package fr.uge.wordrawid.screens.solo

sealed class CaseAction {
    object MoveForward2 : CaseAction()
    object MoveBackward3 : CaseAction()
    object CompassMiniGame : CaseAction()
    object RevealTile : CaseAction()
    object Nothing : CaseAction()
}
