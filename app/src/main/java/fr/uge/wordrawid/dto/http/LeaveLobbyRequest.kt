package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class LeaveLobbyRequest(val lobbyId: Long, val playerId: Long)
