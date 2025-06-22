package fr.uge.wordrawid.solo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import fr.uge.wordrawid.solo.board.BoardGrid
import fr.uge.wordrawid.solo.board.Player

@Composable
fun DiceSection(viewModel: SoloViewModel, onRoll: () -> Unit) {
    DiceWithImage(
        displayResult = viewModel.displayResult,
        onRoll = onRoll,
        rolling = viewModel.rolling
    )
}

@Composable
fun BoardSection(viewModel: SoloViewModel) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = viewModel.randomImageRes),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
        BoardGrid(viewModel.caseMasquee)
        Player(position = viewModel.playerPosition)
    }
}

@Composable
fun GuessSection(viewModel: SoloViewModel, onCheck: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = viewModel.guess,
            onValueChange = { viewModel.guess = it },
            label = { Text("Devine le mot") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.weight(1f).padding(bottom = 20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = { onCheck() }) {
            Text("Valider")
        }
    }
}
