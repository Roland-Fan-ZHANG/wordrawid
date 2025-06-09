package fr.uge.wordrawid.navigation

object Routes {
    const val MENU = "menu"
    const val SOLO = "solo"
    const val MULTI = "multi"
    const val COMPASS = "compass"
    const val CREATE_GAME = "create_game"
    const val JOIN_GAME = "join_game"
    const val LOBBY = "lobby/{gameId}?joinCode={joinCode}&isAdmin={isAdmin}"
    const val GAME = "game/{gameId}"
}
