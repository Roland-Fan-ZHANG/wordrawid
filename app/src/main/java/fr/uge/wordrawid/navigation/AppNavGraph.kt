package fr.uge.wordrawid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.uge.wordrawid.screens.menu.MenuScreen
import fr.uge.wordrawid.screens.minigame.CompassGameScreen
import fr.uge.wordrawid.screens.multi.CreateGameScreen
import fr.uge.wordrawid.screens.multi.GameScreen
import fr.uge.wordrawid.screens.multi.JoinGameScreen
import fr.uge.wordrawid.screens.multi.LobbyScreen
import fr.uge.wordrawid.screens.multi.MultiScreen
import fr.uge.wordrawid.screens.solo.SoloScreen

@Composable
fun AppNavGraph(
  navController: NavHostController = rememberNavController()
) {
  NavHost(navController = navController, startDestination = Routes.MENU) {
    composable(Routes.MENU) { MenuScreen(navController) }
    composable(Routes.SOLO) { SoloScreen() }
    composable(Routes.MULTI) { MultiScreen(navController) }
    composable(Routes.CREATE_GAME) { CreateGameScreen(navController) }
    composable(Routes.JOIN_GAME) { JoinGameScreen(navController) }
    composable(
      route = Routes.LOBBY,
      arguments = listOf(
        navArgument("gameId") { type = NavType.LongType },
        navArgument("joinCode") { type = NavType.StringType },
        navArgument("isAdmin") { type = NavType.BoolType }
      )
    ) {
      LobbyScreen(
        gameId = it.arguments?.getLong("gameId") ?: 0L,
        joinCode = it.arguments?.getString("joinCode") ?: "",
        isAdmin = it.arguments?.getBoolean("isAdmin") == true
      )
    }
    composable(Routes.GAME) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getString("gameId")?.toLong() ?: return@composable
      GameScreen(gameId = gameId, navController = navController)
    }
    composable(Routes.COMPASS) { CompassGameScreen() }
  }
}
