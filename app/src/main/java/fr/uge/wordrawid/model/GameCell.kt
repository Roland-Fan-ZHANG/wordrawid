package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
data class GameCell(
  val id: Long,
  val mysteryWord: MysteryWord,
  var visited: Boolean,
  var successfulPlayerId: Long
)
