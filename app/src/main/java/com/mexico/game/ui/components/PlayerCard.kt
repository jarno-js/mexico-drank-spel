package com.mexico.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mexico.game.data.model.Player
import com.mexico.game.data.model.ScoreResult
import com.mexico.game.ui.theme.*

@Composable
fun PlayerCard(
    player: Player,
    isCurrentPlayer: Boolean = false,
    showScore: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isCurrentPlayer) DarkSurfaceVariant else DarkSurface
            )
            .border(
                width = if (isCurrentPlayer) 3.dp else 1.dp,
                color = if (isCurrentPlayer) AccentPrimary else DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Player name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isCurrentPlayer) AccentPrimary else TextPrimary
                )

                if (isCurrentPlayer) {
                    Text(
                        text = "👉",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Throws used
            if (player.throwsUsed > 0) {
                Text(
                    text = "Worpen: ${player.throwsUsed}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Locked die indicator
            if (player.lockedDie != null) {
                Text(
                    text = "🔒 Vastgezet: ${player.lockedDie}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentSecondary
                )
            }

            // Score
            if (showScore && player.score != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ScoreDisplay(player.score!!)
            }
        }
    }
}

@Composable
private fun ScoreDisplay(score: ScoreResult) {
    val (text, color) = when (score) {
        is ScoreResult.Normal -> score.displayText to TextPrimary
        is ScoreResult.Hundred -> score.displayText to AccentSecondary
        is ScoreResult.Mexico -> score.displayText to SuccessGreen
        is ScoreResult.Sand -> score.displayText to ErrorRed
        is ScoreResult.Pointing -> score.displayText to WarningAmber
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = color
    )
}

@Composable
fun PlayerScoreboardCard(
    player: Player,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = player.name,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        if (player.score != null) {
            val scoreColor = when (player.score) {
                is ScoreResult.Mexico -> SuccessGreen
                is ScoreResult.Sand -> ErrorRed
                is ScoreResult.Hundred -> AccentSecondary
                is ScoreResult.Pointing -> WarningAmber
                else -> TextPrimary
            }

            Text(
                text = player.score!!.displayText,
                style = MaterialTheme.typography.titleMedium,
                color = scoreColor
            )
        } else {
            Text(
                text = "-",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDisabled
            )
        }
    }
}
