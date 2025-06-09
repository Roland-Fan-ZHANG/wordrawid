package fr.uge.wordrawid.dto.http

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(val pseudo: String)
