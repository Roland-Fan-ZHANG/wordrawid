package fr.uge.wordrawid.multi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.uge.wordrawid.navigation.Routes

@Composable
fun MultiScreen(navController: NavController) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Multijoueur",
      fontSize = 28.sp,
      style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(48.dp))
    Button(
      onClick = {navController.navigate(Routes.CREATE_GAME)},
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Créer une partie")
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = { navController.navigate(Routes.JOIN_GAME) },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Rejoindre une partie avec un code")
    }
  }
}
