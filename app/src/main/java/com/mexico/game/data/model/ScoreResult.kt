package com.mexico.game.data.model

sealed class ScoreResult {
    abstract val displayText: String
    abstract val numericValue: Int

    data class Normal(val score: Int) : ScoreResult() {
        override val displayText: String = score.toString()
        override val numericValue: Int = score
    }

    data class Hundred(val value: Int, val drinks: Int) : ScoreResult() {
        override val displayText: String = "${value}00"
        override val numericValue: Int = value * 100
    }

    data object Mexico : ScoreResult() {
        override val displayText: String = "MEXICO!"
        override val numericValue: Int = 10000
    }

    data object Sand : ScoreResult() {
        override val displayText: String = "ZAND!"
        override val numericValue: Int = 0
    }

    data object Pointing : ScoreResult() {
        override val displayText: String = "WIJZEN"
        override val numericValue: Int = -1
    }
}
