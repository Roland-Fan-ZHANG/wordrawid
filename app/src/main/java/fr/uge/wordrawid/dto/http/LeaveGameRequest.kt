package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class LeaveGameRequest(val sessionId: Long, val playerId: Long)
