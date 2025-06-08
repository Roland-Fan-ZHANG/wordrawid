package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
data class MysteryWord(
  val id: Long,
  val word: String,
  val neighbors: List<String>
)
