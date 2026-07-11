package com.shinobisim.model

import kotlinx.serialization.Serializable

@Serializable
data class Shinobi(
    val id: String = "",
    val name: String,
    val clan: Clan,
    val age: Int = 6,
    val rating: Int = 20,
    val skillPoints: Int = 0,
    val clanSkills: Map<String, Int> = emptyMap(),
    val elementalSkills: Map<String, Int> = emptyMap()
) {
    val isMaxAge: Boolean get() = age >= MAX_AGE

    companion object {
        const val START_AGE = 6
        const val MAX_AGE = 50
        const val MIN_RATING = 20
        const val MAX_RATING = 100
    }
}

@Serializable
data class SaveSummary(
    val id: String,
    val name: String,
    val clan: Clan,
    val age: Int,
    val rating: Int
)
