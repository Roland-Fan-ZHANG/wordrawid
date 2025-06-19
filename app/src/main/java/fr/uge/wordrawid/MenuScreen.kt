package fr.uge.wordrawid

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
fun MenuScreen(navController: NavController) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Wordrawid",
      fontSize = 32.sp,
      style = MaterialTheme.typography.headlineMedium
    )

    Spacer(modifier = Modifier.height(48.dp))

    Button(onClick = { navController.navigate(Routes.SOLO) }, modifier = Modifier.fillMaxWidth()) {
      Text("Solo")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { navController.navigate(Routes.MULTI) }, modifier = Modifier.fillMaxWidth()) {
      Text("Multi")
    }
  }
}
