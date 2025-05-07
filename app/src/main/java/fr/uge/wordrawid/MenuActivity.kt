package fr.uge.wordrawid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.wordrawid.ui.theme.WordrawidTheme

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordrawidTheme {
                MenuScreen(
                    onSoloClick = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    },
                    onMultiClick = {
                        // Ajoutez ici l'action pour le bouton Multi
                    }
                )
            }
        }
    }
}

@Composable
fun MenuScreen(onSoloClick: () -> Unit, onMultiClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onSoloClick) {
            Text(text = "Solo")
        }
        Button(onClick = onMultiClick) {
            Text(text = "Multi")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    WordrawidTheme {
        MenuScreen(onSoloClick = {}, onMultiClick = {})
    }
}