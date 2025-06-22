package fr.uge.wordrawid.model

import kotlinx.serialization.Serializable

@Serializable
enum class CellType {
  NEUTRAL,
  MINIGAME1,
  MINIGAME2,
  BONUS,
  MALUS,
}