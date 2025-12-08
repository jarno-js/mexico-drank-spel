package com.mexico.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mexico.game.data.model.GamePhase
import com.mexico.game.ui.components.DiceView
import com.mexico.game.ui.components.PlayerCard
import com.mexico.game.ui.components.PlayerScoreboardCard
import com.mexico.game.ui.components.PotDisplay
import com.mexico.game.ui.theme.DarkBackground
import com.mexico.game.ui.theme.AccentPrimary
import com.mexico.game.ui.theme.TextPrimary
import com.mexico.game.ui.theme.WarningAmber
import com.mexico.game.ui.theme.SuccessGreen
import com.mexico.game.ui.theme.ErrorRed
import com.mexico.game.utils.DiceLogic
import com.mexico.game.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val currentDice by viewModel.currentDice.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val showWijzenPopup by viewModel.showWijzenPopup.collectAsState()
    val showMexicoPopup by viewModel.showMexicoPopup.collectAsState()
    val showSandPopup by viewModel.showSandPopup.collectAsState()
    val showDuimPopup by viewModel.showDuimPopup.collectAsState()
    val currentPlayer = gameState.currentPlayer

    // Wijzen popup
    if (showWijzenPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissWijzenPopup() },
            title = {
                Text(
                    text = "WIJZEN",
                    style = MaterialTheme.typography.displayLarge,
                    color = WarningAmber
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissWijzenPopup() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarningAmber
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Mexico popup
    if (showMexicoPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMexicoPopup() },
            title = {
                Text(
                    text = "MEXICO",
                    style = MaterialTheme.typography.displayLarge,
                    color = SuccessGreen
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissMexicoPopup() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Sand popup
    if (showSandPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSandPopup() },
            title = {
                Text(
                    text = "ZAND",
                    style = MaterialTheme.typography.displayLarge,
                    color = ErrorRed
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissSandPopup() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // DUIM popup
    if (showDuimPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDuimPopup() },
            title = {
                Text(
                    text = "DUIM",
                    style = MaterialTheme.typography.displayLarge,
                    color = AccentPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissDuimPopup() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Death match screen
    if (gameState.phase == GamePhase.DEATH_MATCH) {
        DeathMatchScreen(viewModel)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Ronde ${gameState.roundNumber}",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Pot display
        PotDisplay(potAmount = gameState.pot)

        Spacer(modifier = Modifier.height(16.dp))

        // Current player card
        if (currentPlayer != null) {
            PlayerCard(
                player = currentPlayer,
                isCurrentPlayer = true,
                showScore = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dice area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val dice = currentPlayer.currentDice

                // Check if current roll is Wijzen (1-3 or 3-1) - no locking allowed
                val isWijzen = dice?.let {
                    (it.first == 1 && it.second == 3) || (it.first == 3 && it.second == 1)
                } ?: false

                // Die 1
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val isThisDieLocked = currentPlayer.lockedDiePosition == 1
                    DiceView(
                        value = dice?.first,
                        isRolling = isRolling,
                        isLocked = isThisDieLocked,
                        canLock = !isWijzen && dice?.first?.let { DiceLogic.canLockDie(it) } == true && currentPlayer.lockedDie == null,
                        onLockClick = {
                            if (isThisDieLocked) {
                                viewModel.unlockDie()
                            } else {
                                dice?.first?.let { viewModel.lockDie(it, 1) }
                            }
                        }
                    )
                }

                // Die 2
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val isThisDieLocked = currentPlayer.lockedDiePosition == 2
                    DiceView(
                        value = dice?.second,
                        isRolling = isRolling,
                        isLocked = isThisDieLocked,
                        canLock = !isWijzen && dice?.second?.let { DiceLogic.canLockDie(it) } == true && currentPlayer.lockedDie == null,
                        onLockClick = {
                            if (isThisDieLocked) {
                                viewModel.unlockDie()
                            } else {
                                dice?.second?.let { viewModel.lockDie(it, 2) }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info text
            Text(
                text = "Worpen: ${currentPlayer.throwsUsed}/${gameState.maxThrows}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            if (currentPlayer.throwsUsed < gameState.maxThrows) {
                Button(
                    onClick = { viewModel.rollDice() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    ),
                    enabled = !isRolling
                ) {
                    Text(if (currentPlayer.throwsUsed == 0) "Gooi!" else "Nog een keer!")
                }

                // Add "Stop" button if player has rolled at least once
                if (currentPlayer.hasRolled && currentPlayer.score == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.confirmScore() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Stop")
                    }
                }
            } else if (currentPlayer.hasRolled && currentPlayer.score == null) {
                Button(
                    onClick = { viewModel.confirmScore() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Text("Bevestig Score")
                }
            } else if (currentPlayer.score != null) {
                Button(
                    onClick = { viewModel.nextPlayer() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Text("Volgende Speler")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Scoreboard
        Text(
            text = "Scorebord",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gameState.players) { player ->
                PlayerScoreboardCard(player = player)
            }
        }
    }
}

@Composable
private fun DeathMatchScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val currentIndex = gameState.currentPlayerIndex
    val deathMatchPlayers = gameState.deathMatchPlayers

    if (currentIndex >= deathMatchPlayers.size) {
        return
    }

    val currentPlayer = deathMatchPlayers[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "DEATH MATCH",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gelijke laagste scores!",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = currentPlayer.name,
            style = MaterialTheme.typography.headlineLarge,
            color = AccentPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Dice
        if (currentPlayer.currentDice != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                DiceView(
                    value = currentPlayer.currentDice!!.first,
                    isRolling = false
                )
                DiceView(
                    value = currentPlayer.currentDice!!.second,
                    isRolling = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentPlayer.score != null) {
                Text(
                    text = "Score: ${currentPlayer.score!!.displayText}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AccentPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!currentPlayer.hasRolled) {
            Button(
                onClick = { viewModel.rollDeathMatchDice() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary
                ),
                enabled = !isRolling
            ) {
                Text("Gooi!")
            }
        } else {
            Button(
                onClick = { viewModel.nextDeathMatchPlayer() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary
                )
            ) {
                Text(if (currentIndex < deathMatchPlayers.size - 1) "Volgende Speler" else "Resultaten")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Show all death match players
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            deathMatchPlayers.forEach { player ->
                PlayerScoreboardCard(player = player)
            }
        }
    }
}
