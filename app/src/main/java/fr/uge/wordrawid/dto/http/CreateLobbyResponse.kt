package fr.uge.wordrawid.dto.http

import fr.uge.wordrawid.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class CreateLobbyResponse(val player: Player, val joinCode: String, val lobbyId: Long)
