package fr.uge.wordrawid.dto.ws

import fr.uge.wordrawid.model.Lobby
import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(
  val lobby: Lobby,
  val imageUrl: String
)
