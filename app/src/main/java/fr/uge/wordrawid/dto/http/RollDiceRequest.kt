package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class RollDiceRequest(
  val lobbyId: Long,
  val playerId: Long
)
