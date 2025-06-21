package fr.uge.wordrawid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.uge.wordrawid.MenuScreen
import fr.uge.wordrawid.minigame.BalloonGameScreen
import fr.uge.wordrawid.minigame.CompassGameScreen
import fr.uge.wordrawid.multi.CreateGameScreen
import fr.uge.wordrawid.multi.GameScreen
import fr.uge.wordrawid.multi.JoinGameScreen
import fr.uge.wordrawid.multi.LobbyScreen
import fr.uge.wordrawid.multi.MultiScreen
import fr.uge.wordrawid.solo.SoloScreen
import fr.uge.wordrawid.solo.WinScreen

@Composable
@androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
fun AppNavGraph(
  navController: NavHostController = rememberNavController()
) {
  NavHost(navController = navController, startDestination = Routes.MENU) {
    composable(Routes.MENU) { MenuScreen(navController) }
    composable(Routes.SOLO) { SoloScreen(navController) }
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
        isAdmin = it.arguments?.getBoolean("isAdmin") == true,
      )
    }
    composable(Routes.GAME) { backStackEntry ->
      val gameId = backStackEntry.arguments?.getString("gameId")?.toLong() ?: return@composable
      GameScreen(gameId = gameId, navController = navController)
    }
    composable(Routes.COMPASS) { CompassGameScreen(navController) }
    composable(Routes.BALLOON) { BalloonGameScreen(navController) }
    composable(Routes.WIN) { WinScreen(navController) }
  }
}
