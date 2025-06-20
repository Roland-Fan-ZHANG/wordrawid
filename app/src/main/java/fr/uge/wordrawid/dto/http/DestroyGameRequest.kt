package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class DestroyGameRequest(val sessionId: Long)
