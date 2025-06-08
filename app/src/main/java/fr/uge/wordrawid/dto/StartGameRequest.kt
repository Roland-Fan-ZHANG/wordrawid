package fr.uge.wordrawid.dto

import fr.uge.wordrawid.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class StartGameRequest(val admin: Player, val gameId: Long)
