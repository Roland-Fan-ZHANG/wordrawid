package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
data class Cell(
  val id: Long,
  val cellType: CellType,
  val mysteryWord: MysteryWord,
  var visited: Boolean,
  var successfulPlayerId: Long
)
