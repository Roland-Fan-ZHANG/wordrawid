package fr.uge.wordrawid.dto.http

import fr.uge.wordrawid.model.CellType
import kotlinx.serialization.Serializable

@Serializable
data class BonusMalusMoveRequest(
  val cellType: CellType,
  val lobbyId: Long,
  val playerId: Long,
)
