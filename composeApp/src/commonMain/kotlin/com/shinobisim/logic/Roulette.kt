package com.shinobisim.logic

import kotlin.random.Random

object Roulette {

    data class Outcome(val points: Int, val weight: Int)

    val outcomes = listOf(
        Outcome(1, 25),
        Outcome(2, 20),
        Outcome(3, 16),
        Outcome(4, 13),
        Outcome(5, 10),
        Outcome(6, 7),
        Outcome(7, 5),
        Outcome(8, 2),
        Outcome(9, 1),
        Outcome(10, 1)
    )

    private val totalWeight = outcomes.sumOf { it.weight }

    fun spin(): Int {
        var roll = Random.nextInt(totalWeight)
        for (outcome in outcomes) {
            roll -= outcome.weight
            if (roll < 0) return outcome.points
        }
        return outcomes.last().points
    }

    fun probability(points: Int): Double {
        val outcome = outcomes.find { it.points == points } ?: return 0.0
        return outcome.weight.toDouble() / totalWeight
    }
}
