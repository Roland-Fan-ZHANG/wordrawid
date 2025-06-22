package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class JoinLobbyRequest(val pseudo: String, val joinCode: String)
