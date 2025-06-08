package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
data class GameManager(
  val id: Long,
  val board: List<GameCell>,
  val finalWord: MysteryWord
)
