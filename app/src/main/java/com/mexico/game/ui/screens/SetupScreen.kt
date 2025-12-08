package com.mexico.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mexico.game.ui.theme.DarkBackground
import com.mexico.game.ui.theme.AccentPrimary
import com.mexico.game.ui.theme.TextPrimary
import com.mexico.game.viewmodel.GameViewModel

@Composable
fun SetupScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    var playerName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "MEXICO",
            style = MaterialTheme.typography.displayLarge,
            color = AccentPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Dobbelsteenspel",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Player input
        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Speler naam") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPrimary,
                focusedLabelColor = AccentPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (playerName.isNotBlank()) {
                    viewModel.addPlayer(playerName)
                    playerName = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPrimary
            ),
            enabled = playerName.isNotBlank() && gameState.players.size < 10
        ) {
            Text("Speler Toevoegen")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Player list
        Text(
            text = "Spelers (${gameState.players.size}/10)",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gameState.players) { player ->
                PlayerListItem(
                    playerName = player.name,
                    onRemove = { viewModel.removePlayer(player.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start button
        Button(
            onClick = {
                viewModel.startInitialRoll()
                onStartGame()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPrimary
            ),
            enabled = gameState.players.size >= 2
        ) {
            Text(
                text = "Start Spel",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (gameState.players.size < 2) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Minimaal 2 spelers nodig",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PlayerListItem(
    playerName: String,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = playerName,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

            TextButton(onClick = onRemove) {
                Text("Verwijder", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
