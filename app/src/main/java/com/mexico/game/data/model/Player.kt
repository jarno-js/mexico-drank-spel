package com.mexico.game.data.model

data class Player(
    val id: String,
    val name: String,
    var score: ScoreResult? = null,
    var throwsUsed: Int = 0,
    var lockedDie: Int? = null,
    var lockedDiePosition: Int? = null, // 1 = first die, 2 = second die
    var hasRolled: Boolean = false,
    var initialRoll: Int? = null,
    var currentDice: Pair<Int, Int>? = null,
    var previousDice: Pair<Int, Int>? = null, // Previous throw for DUIM detection
    var isEliminated: Boolean = false
) {
    fun reset() {
        score = null
        throwsUsed = 0
        lockedDie = null
        lockedDiePosition = null
        hasRolled = false
        currentDice = null
        previousDice = null
    }

    fun resetForNewRound() {
        score = null
        throwsUsed = 0
        lockedDie = null
        lockedDiePosition = null
        hasRolled = false
        currentDice = null
        previousDice = null
        isEliminated = false
    }
}
