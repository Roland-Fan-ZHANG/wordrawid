package fr.uge.wordrawid.dto

import kotlinx.serialization.Serializable

@Serializable
data class JoinGameRequest(val pseudo: String, val joinCode: String)
