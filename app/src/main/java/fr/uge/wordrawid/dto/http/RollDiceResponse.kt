package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class RollDiceResponse(
  val diceResult: Int,
)
