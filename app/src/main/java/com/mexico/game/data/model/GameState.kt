package com.mexico.game.data.model

data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val pot: Int = 0,
    val maxThrows: Int = 0,
    val mexicoMode: Boolean = false,
    val phase: GamePhase = GamePhase.SETUP,
    val roundNumber: Int = 1,
    val deathMatchPlayers: List<Player> = emptyList()
) {
    val currentPlayer: Player?
        get() = if (phase == GamePhase.DEATH_MATCH) {
            deathMatchPlayers.getOrNull(currentPlayerIndex)
        } else {
            players.getOrNull(currentPlayerIndex)
        }

    val activePlayers: List<Player>
        get() = if (phase == GamePhase.DEATH_MATCH) {
            deathMatchPlayers
        } else {
            players
        }

    fun getLowestScorePlayers(): List<Player> {
        val playersWithScores = players.filter {
            it.score != null && it.score !is ScoreResult.Pointing
        }

        if (playersWithScores.isEmpty()) return emptyList()

        val minScore = playersWithScores.minOfOrNull { it.score!!.numericValue } ?: return emptyList()
        return playersWithScores.filter { it.score!!.numericValue == minScore }
    }
}
