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
import com.mexico.game.data.model.ScoreResult
import com.mexico.game.ui.components.PlayerScoreboardCard
import com.mexico.game.ui.theme.*
import com.mexico.game.viewmodel.GameViewModel

@Composable
fun ResultScreen(
    viewModel: GameViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val losers = gameState.getLowestScorePlayers()
    val pot = gameState.pot

    val hasSand = losers.any { it.score is ScoreResult.Sand }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "EINDE RONDE ${gameState.roundNumber}",
            style = MaterialTheme.typography.displayMedium,
            color = AccentPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Pot display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🍺",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "$pot slokken",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SuccessGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Loser(s) announcement
        if (losers.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ErrorRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (losers.size > 1) "VERLIEZERS" else "VERLIEZER",
                        style = MaterialTheme.typography.titleLarge,
                        color = ErrorRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    losers.forEach { loser ->
                        Text(
                            text = loser.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (hasSand) {
                        Text(
                            text = "ZAND! 💀",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ErrorRed
                        )
                        Text(
                            text = "Drink een half atje direct!",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "Drink de pot!",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "$pot slokken",
                            style = MaterialTheme.typography.headlineLarge,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Final scoreboard
        Text(
            text = "Eindstand",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gameState.players.sortedBy { it.score?.numericValue ?: Int.MAX_VALUE }) { player ->
                PlayerScoreboardCard(player = player)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // New round button
        Button(
            onClick = { viewModel.startNewRound() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPrimary
            )
        ) {
            Text(
                text = "Nieuwe Ronde",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
