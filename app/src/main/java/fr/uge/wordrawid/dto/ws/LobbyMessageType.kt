package fr.uge.wordrawid.dto.ws

import kotlinx.serialization.Serializable

@Serializable
enum class LobbyMessageType {
  JOIN, LEAVE, START
}
