package com.mexico.game.viewmodel

import androidx.lifecycle.ViewModel
import com.mexico.game.data.model.*
import com.mexico.game.utils.DiceLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _currentDice = MutableStateFlow<Pair<Int, Int>?>(null)
    val currentDice: StateFlow<Pair<Int, Int>?> = _currentDice.asStateFlow()

    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private val _showWijzenPopup = MutableStateFlow(false)
    val showWijzenPopup: StateFlow<Boolean> = _showWijzenPopup.asStateFlow()

    private val _showMexicoPopup = MutableStateFlow(false)
    val showMexicoPopup: StateFlow<Boolean> = _showMexicoPopup.asStateFlow()

    private val _showSandPopup = MutableStateFlow(false)
    val showSandPopup: StateFlow<Boolean> = _showSandPopup.asStateFlow()

    private val _showDuimPopup = MutableStateFlow(false)
    val showDuimPopup: StateFlow<Boolean> = _showDuimPopup.asStateFlow()

    sealed class NavigationEvent {
        data object ToInitialRoll : NavigationEvent()
        data object ToGame : NavigationEvent()
        data object ToResult : NavigationEvent()
    }

    // Setup Phase
    fun addPlayer(name: String) {
        if (name.isBlank() || _gameState.value.players.size >= 10) return

        val player = Player(
            id = UUID.randomUUID().toString(),
            name = name.trim()
        )

        _gameState.value = _gameState.value.copy(
            players = _gameState.value.players + player
        )
    }

    fun removePlayer(playerId: String) {
        _gameState.value = _gameState.value.copy(
            players = _gameState.value.players.filter { it.id != playerId }
        )
    }

    fun startInitialRoll() {
        if (_gameState.value.players.size < 2) return

        _gameState.value = _gameState.value.copy(
            phase = GamePhase.INITIAL_ROLL,
            currentPlayerIndex = 0
        )
        _navigationEvent.value = NavigationEvent.ToInitialRoll
    }

    // Initial Roll Phase
    fun performInitialRoll() {
        val currentPlayer = _gameState.value.currentPlayer ?: return
        if (currentPlayer.initialRoll != null) return

        _isRolling.value = true
        val roll = DiceLogic.rollSingleDie()

        val updatedPlayers = _gameState.value.players.map {
            if (it.id == currentPlayer.id) {
                it.copy(initialRoll = roll)
            } else it
        }

        _gameState.value = _gameState.value.copy(players = updatedPlayers)
        _isRolling.value = false
    }

    fun nextInitialRoll() {
        val nextIndex = _gameState.value.currentPlayerIndex + 1

        if (nextIndex >= _gameState.value.players.size) {
            // All players rolled, determine order
            determinePlayOrder()
        } else {
            _gameState.value = _gameState.value.copy(currentPlayerIndex = nextIndex)
        }
    }

    private fun determinePlayOrder() {
        val sortedPlayers = _gameState.value.players.sortedByDescending { it.initialRoll ?: 0 }

        _gameState.value = _gameState.value.copy(
            players = sortedPlayers,
            currentPlayerIndex = 0,
            phase = GamePhase.ROLLING,
            maxThrows = 3  // Always allow up to 3 throws
        )

        // Reset all players for the round
        val resetPlayers = _gameState.value.players.map { it.copy().apply { reset() } }
        _gameState.value = _gameState.value.copy(players = resetPlayers)

        _navigationEvent.value = NavigationEvent.ToGame
    }

    // Rolling Phase
    fun rollDice() {
        val currentPlayer = _gameState.value.currentPlayer ?: return
        val maxThrows = _gameState.value.maxThrows

        if (currentPlayer.throwsUsed >= maxThrows) return

        _isRolling.value = true

        // If a die is locked, only roll the other one
        val newDice = if (currentPlayer.lockedDie != null && currentPlayer.lockedDiePosition != null) {
            val lockedValue = currentPlayer.lockedDie!!
            val lockedPosition = currentPlayer.lockedDiePosition!!
            val newDie = DiceLogic.rollSingleDie()

            // Keep locked die in its original position
            if (lockedPosition == 1) {
                Pair(lockedValue, newDie)  // First die locked, second die rolls
            } else {
                Pair(newDie, lockedValue)  // Second die locked, first die rolls
            }
        } else {
            DiceLogic.rollTwoDice()
        }

        _currentDice.value = newDice

        // Check for DUIM (same throw twice in a row)
        val isDuim = currentPlayer.previousDice?.let { prev ->
            (prev.first == newDice.first && prev.second == newDice.second) ||
            (prev.first == newDice.second && prev.second == newDice.first)
        } ?: false

        // Update the correct player list based on phase
        if (_gameState.value.phase == GamePhase.DEATH_MATCH) {
            val updatedDeathMatchPlayers = _gameState.value.deathMatchPlayers.map {
                if (it.id == currentPlayer.id) {
                    it.copy(
                        currentDice = newDice,
                        previousDice = newDice,
                        throwsUsed = it.throwsUsed + 1,
                        hasRolled = true
                    )
                } else it
            }
            _gameState.value = _gameState.value.copy(deathMatchPlayers = updatedDeathMatchPlayers)
        } else {
            val updatedPlayers = _gameState.value.players.map {
                if (it.id == currentPlayer.id) {
                    it.copy(
                        currentDice = newDice,
                        previousDice = newDice,
                        throwsUsed = it.throwsUsed + 1,
                        hasRolled = true
                    )
                } else it
            }
            _gameState.value = _gameState.value.copy(players = updatedPlayers)
        }

        _isRolling.value = false

        // Check for DUIM first
        if (isDuim) {
            _showDuimPopup.value = true
            return
        }

        // Check for special rolls
        val score = DiceLogic.calculateScore(newDice.first, newDice.second)
        when (score) {
            is ScoreResult.Pointing -> {
                _showWijzenPopup.value = true
            }
            is ScoreResult.Mexico -> {
                _showMexicoPopup.value = true
            }
            is ScoreResult.Sand -> {
                _showSandPopup.value = true
            }
            else -> {
                // Normal roll, player can continue or stop
            }
        }
    }

    fun dismissWijzenPopup() {
        _showWijzenPopup.value = false

        // Wijzen doesn't count as a throw - subtract 1 from throwsUsed and reset dice
        val currentPlayer = _gameState.value.currentPlayer ?: return

        if (_gameState.value.phase == GamePhase.DEATH_MATCH) {
            val updatedDeathMatchPlayers = _gameState.value.deathMatchPlayers.map {
                if (it.id == currentPlayer.id) {
                    it.copy(
                        throwsUsed = maxOf(0, it.throwsUsed - 1),
                        currentDice = null,
                        lockedDie = null,
                        lockedDiePosition = null
                    )
                } else it
            }
            _gameState.value = _gameState.value.copy(deathMatchPlayers = updatedDeathMatchPlayers)
        } else {
            val updatedPlayers = _gameState.value.players.map {
                if (it.id == currentPlayer.id) {
                    it.copy(
                        throwsUsed = maxOf(0, it.throwsUsed - 1),
                        currentDice = null,
                        lockedDie = null,
                        lockedDiePosition = null
                    )
                } else it
            }
            _gameState.value = _gameState.value.copy(players = updatedPlayers)
        }
        _currentDice.value = null
    }

    fun dismissMexicoPopup() {
        _showMexicoPopup.value = false

        // Mexico throws limit rule: ONLY if first player (index 0) throws Mexico,
        // set maxThrows to how many throws they used
        val currentPlayerIndex = _gameState.value.currentPlayerIndex
        val currentPlayer = _gameState.value.currentPlayer
        if (currentPlayerIndex == 0 && currentPlayer != null) {
            _gameState.value = _gameState.value.copy(
                maxThrows = currentPlayer.throwsUsed
            )
        }

        confirmScore()
    }

    fun dismissSandPopup() {
        _showSandPopup.value = false

        // Zand throws limit rule: ONLY if first player (index 0) throws Zand,
        // set maxThrows to how many throws they used (same as Mexico)
        val currentPlayerIndex = _gameState.value.currentPlayerIndex
        val currentPlayer = _gameState.value.currentPlayer
        if (currentPlayerIndex == 0 && currentPlayer != null) {
            _gameState.value = _gameState.value.copy(
                maxThrows = currentPlayer.throwsUsed
            )
        }

        confirmScore()
    }

    fun dismissDuimPopup() {
        _showDuimPopup.value = false
        // DUIM is just a notification, throw counts normally
    }

    fun lockDie(die: Int, position: Int) {
        val currentPlayer = _gameState.value.currentPlayer ?: return

        if (!DiceLogic.canLockDie(die) || currentPlayer.lockedDie != null) return

        if (_gameState.value.phase == GamePhase.DEATH_MATCH) {
            val updatedDeathMatchPlayers = _gameState.value.deathMatchPlayers.map {
                if (it.id == currentPlayer.id) {
                    it.copy(lockedDie = die, lockedDiePosition = position)
                } else it
            }
            _gameState.value = _gameState.value.copy(deathMatchPlayers = updatedDeathMatchPlayers)
        } else {
            val updatedPlayers = _gameState.value.players.map {
                if (it.id == currentPlayer.id) {
                    it.copy(lockedDie = die, lockedDiePosition = position)
                } else it
            }
            _gameState.value = _gameState.value.copy(players = updatedPlayers)
        }
    }

    fun unlockDie() {
        val currentPlayer = _gameState.value.currentPlayer ?: return

        if (_gameState.value.phase == GamePhase.DEATH_MATCH) {
            val updatedDeathMatchPlayers = _gameState.value.deathMatchPlayers.map {
                if (it.id == currentPlayer.id) {
                    it.copy(lockedDie = null, lockedDiePosition = null)
                } else it
            }
            _gameState.value = _gameState.value.copy(deathMatchPlayers = updatedDeathMatchPlayers)
        } else {
            val updatedPlayers = _gameState.value.players.map {
                if (it.id == currentPlayer.id) {
                    it.copy(lockedDie = null, lockedDiePosition = null)
                } else it
            }
            _gameState.value = _gameState.value.copy(players = updatedPlayers)
        }
    }

    fun confirmScore() {
        val currentPlayer = _gameState.value.currentPlayer ?: return
        val dice = currentPlayer.currentDice ?: return

        val score = DiceLogic.calculateScore(dice.first, dice.second)

        if (_gameState.value.phase == GamePhase.DEATH_MATCH) {
            // In death match, just update score - no pot changes
            val updatedDeathMatchPlayers = _gameState.value.deathMatchPlayers.map {
                if (it.id == currentPlayer.id) {
                    it.copy(score = score)
                } else it
            }

            _gameState.value = _gameState.value.copy(
                deathMatchPlayers = updatedDeathMatchPlayers
            )

            // First player sets the max throws for everyone else in death match
            val currentPlayerIndex = _gameState.value.currentPlayerIndex
            if (currentPlayerIndex == 0) {
                _gameState.value = _gameState.value.copy(
                    maxThrows = currentPlayer.throwsUsed
                )
            }
        } else {
            // Normal game logic
            // Handle Mexico
            if (score is ScoreResult.Mexico) {
                val newPot = _gameState.value.pot + 5

                // Remove all hundreds
                val updatedPlayers = _gameState.value.players.map {
                    if (it.id == currentPlayer.id) {
                        it.copy(score = score)
                    } else if (it.score is ScoreResult.Hundred) {
                        it.copy(score = null)
                    } else it
                }

                _gameState.value = _gameState.value.copy(
                    players = updatedPlayers,
                    pot = newPot,
                    mexicoMode = true
                )
            } else {
                // Handle Hundred pot addition (but NOT for Sand)
                val potAddition = if (score is ScoreResult.Hundred) score.drinks else 0
                val newPot = _gameState.value.pot + potAddition

                val updatedPlayers = _gameState.value.players.map {
                    if (it.id == currentPlayer.id) {
                        it.copy(score = score)
                    } else it
                }

                _gameState.value = _gameState.value.copy(
                    players = updatedPlayers,
                    pot = newPot
                )
            }

            // First player sets the max throws for everyone else
            val currentPlayerIndex = _gameState.value.currentPlayerIndex
            if (currentPlayerIndex == 0) {
                _gameState.value = _gameState.value.copy(
                    maxThrows = currentPlayer.throwsUsed
                )
            }
        }

        // Move to next player or end round
        nextPlayer()
    }

    fun nextPlayer() {
        val nextIndex = _gameState.value.currentPlayerIndex + 1

        if (_gameState.value.phase == GamePhase.DEATH_MATCH) {
            // Check if all death match players have played
            if (nextIndex >= _gameState.value.deathMatchPlayers.size) {
                finishDeathMatch()
            } else {
                _gameState.value = _gameState.value.copy(currentPlayerIndex = nextIndex)
                _currentDice.value = null
            }
        } else {
            // Normal game flow
            if (nextIndex >= _gameState.value.players.size) {
                endRound()
            } else {
                _gameState.value = _gameState.value.copy(currentPlayerIndex = nextIndex)
                _currentDice.value = null
            }
        }
    }

    private fun endRound() {
        val losers = _gameState.value.getLowestScorePlayers()

        if (losers.size > 1) {
            // Death match - play full game with only these players
            val resetDeathMatchPlayers = losers.map {
                it.copy().apply { reset() }
            }

            _gameState.value = _gameState.value.copy(
                phase = GamePhase.DEATH_MATCH,
                deathMatchPlayers = resetDeathMatchPlayers,
                currentPlayerIndex = 0,
                maxThrows = 3  // Reset to 3 throws for death match
            )
            _currentDice.value = null
        } else {
            _gameState.value = _gameState.value.copy(phase = GamePhase.ROUND_END)
            _navigationEvent.value = NavigationEvent.ToResult
        }
    }

    private fun finishDeathMatch() {
        val deathMatchPlayers = _gameState.value.deathMatchPlayers

        // Find players with lowest score in death match
        val playersWithScores = deathMatchPlayers.filter { it.score != null }
        if (playersWithScores.isEmpty()) return

        val lowestScore = playersWithScores.minOfOrNull { it.score!!.numericValue } ?: return
        val finalLosers = playersWithScores.filter { it.score!!.numericValue == lowestScore }

        // Update main player list with death match results
        val updatedPlayers = _gameState.value.players.map { player ->
            val deathMatchPlayer = deathMatchPlayers.find { it.id == player.id }
            if (deathMatchPlayer != null) {
                player.copy(score = deathMatchPlayer.score)
            } else player
        }

        // Check if there's still a tie among death match players
        if (finalLosers.size > 1) {
            // Another death match needed!
            val resetDeathMatchPlayers = finalLosers.map {
                it.copy().apply { reset() }
            }

            _gameState.value = _gameState.value.copy(
                players = updatedPlayers,
                deathMatchPlayers = resetDeathMatchPlayers,
                currentPlayerIndex = 0,
                maxThrows = 3
            )
            _currentDice.value = null
        } else {
            // We have a single loser, end the round
            _gameState.value = _gameState.value.copy(
                players = updatedPlayers,
                phase = GamePhase.ROUND_END
            )
            _navigationEvent.value = NavigationEvent.ToResult
        }
    }

    fun startNewRound() {
        val resetPlayers = _gameState.value.players.map {
            it.copy().apply { resetForNewRound() }
        }

        _gameState.value = _gameState.value.copy(
            players = resetPlayers,
            currentPlayerIndex = 0,
            pot = 0,
            maxThrows = 3,  // Always allow up to 3 throws
            mexicoMode = false,
            phase = GamePhase.ROLLING,
            roundNumber = _gameState.value.roundNumber + 1,
            deathMatchPlayers = emptyList()
        )

        _currentDice.value = null
        _navigationEvent.value = NavigationEvent.ToGame
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
}
