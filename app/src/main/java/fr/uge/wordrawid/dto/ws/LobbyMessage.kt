package fr.uge.wordrawid.dto.ws

import fr.uge.wordrawid.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class LobbyMessage(
  val lobbyMessageType: LobbyMessageType,
  val player: Player,
  val content: String
)
