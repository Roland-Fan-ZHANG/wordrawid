package fr.uge.wordrawid.dto.ws

import fr.uge.wordrawid.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class SpecialCellMessage(
  val player: Player,
  val content: String
)
