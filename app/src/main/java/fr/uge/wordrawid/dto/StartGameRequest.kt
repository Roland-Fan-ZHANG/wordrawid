package fr.uge.wordrawid.dto

import kotlinx.serialization.Serializable

@Serializable
data class StartGameRequest(val adminName: String, val gameId: Long)
