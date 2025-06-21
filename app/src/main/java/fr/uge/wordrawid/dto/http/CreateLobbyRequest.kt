package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class CreateLobbyRequest(val pseudo: String)
