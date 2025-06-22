package fr.uge.wordrawid.solo.board

import kotlin.random.Random

val boardActions: List<CaseAction> by lazy {
    val actions = mutableListOf<CaseAction>().apply {
        addAll(List(3) { CaseAction.MoveForward2 })
        addAll(List(4) { CaseAction.MoveBackward3 })
        addAll(List(3) { CaseAction.CompassMiniGame })
        addAll(List(4) { CaseAction.RevealTile })
        addAll(List(3) { CaseAction.BalloonMiniGame })
        addAll(List(8) { CaseAction.Nothing })
    }
    actions.shuffled(Random.Default)
}
