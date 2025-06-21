package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
  val id: Long,
  val name: String,
  val position: Int,
)
