package fr.uge.wordrawid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.uge.wordrawid.screens.multi.CreateGameScreen
import fr.uge.wordrawid.screens.multi.LobbyScreen
import fr.uge.wordrawid.ui.screens.menu.MenuScreen
import fr.uge.wordrawid.ui.screens.minigame.CompassGameScreen
import fr.uge.wordrawid.ui.screens.solo.SoloScreen
import fr.uge.wordrawid.screens.multi.MultiScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.MENU) {
        composable(Routes.MENU) { MenuScreen(navController) }
        composable(Routes.SOLO) { SoloScreen() }
        composable(Routes.MULTI) { MultiScreen(navController) }
        composable(Routes.CREATE_GAME) { CreateGameScreen(navController) }
        composable(Routes.LOBBY) { LobbyScreen() }
        composable(Routes.COMPASS) { CompassGameScreen() }
    }
}