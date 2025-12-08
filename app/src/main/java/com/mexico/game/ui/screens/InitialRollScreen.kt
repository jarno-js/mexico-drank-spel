package com.mexico.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mexico.game.ui.components.DiceView
import com.mexico.game.ui.theme.DarkBackground
import com.mexico.game.ui.theme.AccentPrimary
import com.mexico.game.ui.theme.TextPrimary
import com.mexico.game.ui.theme.TextSecondary
import com.mexico.game.viewmodel.GameViewModel

@Composable
fun InitialRollScreen(
    viewModel: GameViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val currentPlayer = gameState.currentPlayer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bepaal Volgorde",
            style = MaterialTheme.typography.displayMedium,
            color = AccentPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Hoogste getal begint",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (currentPlayer != null) {
            Text(
                text = currentPlayer.name,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            DiceView(
                value = currentPlayer.initialRoll,
                isRolling = isRolling,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (currentPlayer.initialRoll == null) {
                Button(
                    onClick = { viewModel.performInitialRoll() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    ),
                    enabled = !isRolling
                ) {
                    Text("Gooi Dobbelsteen")
                }
            } else {
                Button(
                    onClick = { viewModel.nextInitialRoll() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Text("Volgende Speler")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Show all players and their rolls
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gameState.players) { player ->
                InitialRollPlayerCard(
                    playerName = player.name,
                    roll = player.initialRoll,
                    isCurrent = player.id == currentPlayer?.id
                )
            }
        }
    }
}

@Composable
private fun InitialRollPlayerCard(
    playerName: String,
    roll: Int?,
    isCurrent: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isCurrent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
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
                color = if (isCurrent) AccentPrimary else TextPrimary
            )

            Text(
                text = roll?.toString() ?: "-",
                style = MaterialTheme.typography.titleLarge,
                color = if (roll != null) AccentPrimary else TextSecondary
            )
        }
    }
}
