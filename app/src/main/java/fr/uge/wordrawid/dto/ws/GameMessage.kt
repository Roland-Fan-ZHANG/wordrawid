package fr.uge.wordrawid.dto.ws

import fr.uge.wordrawid.model.Session
import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(
  val session: Session,
  val imageUrl: String
)
