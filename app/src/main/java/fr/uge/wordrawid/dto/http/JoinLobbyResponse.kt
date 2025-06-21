package fr.uge.wordrawid.dto.http

import fr.uge.wordrawid.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class JoinLobbyResponse(
  val player: Player,
  val joinCode: String,
  val gameId: Long,
  val otherPlayers: List<Player>
)
