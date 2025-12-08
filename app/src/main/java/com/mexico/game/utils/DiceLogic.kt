package com.mexico.game.utils

import com.mexico.game.data.model.ScoreResult
import kotlin.random.Random

object DiceLogic {

    fun rollSingleDie(): Int = Random.nextInt(1, 7)

    fun rollTwoDice(): Pair<Int, Int> = Pair(rollSingleDie(), rollSingleDie())

    fun canLockDie(die: Int): Boolean = die == 1 || die == 2

    fun calculateScore(die1: Int, die2: Int): ScoreResult {
        // Check for Mexico (1+2)
        if ((die1 == 1 && die2 == 2) || (die1 == 2 && die2 == 1)) {
            return ScoreResult.Mexico
        }

        // Check for Zand (2+3)
        if ((die1 == 2 && die2 == 3) || (die1 == 3 && die2 == 2)) {
            return ScoreResult.Sand
        }

        // Check for Wijzen (1+3)
        if ((die1 == 1 && die2 == 3) || (die1 == 3 && die2 == 1)) {
            return ScoreResult.Pointing
        }

        // Check for Honderdtallen (doubles)
        if (die1 == die2) {
            return ScoreResult.Hundred(value = die1, drinks = die1)
        }

        // Normal score: highest first
        val higher = maxOf(die1, die2)
        val lower = minOf(die1, die2)
        val normalScore = higher * 10 + lower

        return ScoreResult.Normal(normalScore)
    }

    fun isSpecialRoll(score: ScoreResult): Boolean {
        return when (score) {
            is ScoreResult.Mexico,
            is ScoreResult.Sand,
            is ScoreResult.Pointing -> true
            else -> false
        }
    }

    fun getScoreDescription(score: ScoreResult): String {
        return when (score) {
            is ScoreResult.Normal -> "Score: ${score.score}"
            is ScoreResult.Hundred -> "${score.value}00 (${score.drinks} slokken)"
            is ScoreResult.Mexico -> "MEXICO! (5 slokken in pot, 100-tallen weg)"
            is ScoreResult.Sand -> "ZAND! (Direct half atje drinken)"
            is ScoreResult.Pointing -> "WIJZEN! (Wijs naar spelers)"
        }
    }

    fun shouldRemoveHundreds(mexicoRolled: Boolean): Boolean = mexicoRolled

    fun getDrinksForLoser(pot: Int, hasSand: Boolean): String {
        return if (hasSand) {
            "Half atje direct"
        } else {
            "$pot slokken uit de pot"
        }
    }
}
