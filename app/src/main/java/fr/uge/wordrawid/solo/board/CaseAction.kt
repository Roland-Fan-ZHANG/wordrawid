package fr.uge.wordrawid.solo.board

sealed class CaseAction {
    data object MoveForward2 : CaseAction()
    data object MoveBackward3 : CaseAction()
    data object CompassMiniGame : CaseAction()
    data object BalloonMiniGame : CaseAction()
    data object RevealTile : CaseAction()
    data object Nothing : CaseAction()
}
