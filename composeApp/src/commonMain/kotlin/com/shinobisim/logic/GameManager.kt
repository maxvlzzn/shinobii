package com.shinobisim.logic

import com.shinobisim.model.Clan
import com.shinobisim.model.ClanSkillTrees
import com.shinobisim.model.ElementalSkillTree
import com.shinobisim.model.Shinobi
import com.shinobisim.model.Skill
import com.shinobisim.network.SupabaseRepository

class GameManager(
    private val repository: SupabaseRepository = SupabaseRepository()
) {

    suspend fun hasSavedGame(): Boolean = repository.listSaves().isNotEmpty()

    suspend fun listSaves(): List<com.shinobisim.model.SaveSummary> = repository.listSaves()

    suspend fun getLatestSaveSummary(): com.shinobisim.model.SaveSummary? =
        repository.listSaves().firstOrNull()

    suspend fun loadLatestSave(): Shinobi? {
        val summary = getLatestSaveSummary() ?: return null
        return repository.loadSave(summary.id)
    }

    suspend fun loadSave(id: String): Shinobi? = repository.loadSave(id)

    suspend fun startNewGame(name: String, clan: Clan): Shinobi {
        val shinobi = Shinobi(name = name, clan = clan)
        return repository.createSave(shinobi)
    }

    suspend fun updateShinobi(shinobi: Shinobi): Boolean {
        return repository.updateSave(shinobi)
    }

    suspend fun deleteSave(id: String): Boolean {
        return repository.deleteSave(id)
    }

    fun spinRoulette(shinobi: Shinobi): Pair<Shinobi, Int> {
        val points = Roulette.spin()
        return shinobi.copy(skillPoints = shinobi.skillPoints + points) to points
    }

    fun increaseAge(shinobi: Shinobi): Shinobi {
        if (shinobi.isMaxAge) return shinobi
        return shinobi.copy(age = shinobi.age + 1)
    }

    fun canIncreaseRating(shinobi: Shinobi): Boolean {
        return shinobi.skillPoints > 0 && shinobi.rating < Shinobi.MAX_RATING
    }

    fun increaseRating(shinobi: Shinobi): Shinobi? {
        if (!canIncreaseRating(shinobi)) return null
        return shinobi.copy(
            rating = (shinobi.rating + 5).coerceAtMost(Shinobi.MAX_RATING),
            skillPoints = shinobi.skillPoints - 1
        )
    }

    fun canUpgradeClanSkill(shinobi: Shinobi, skill: Skill): Boolean {
        val currentLevel = shinobi.clanSkills[skill.id] ?: 0
        return currentLevel < skill.maxLevel && shinobi.skillPoints >= skill.costPerLevel
    }

    fun upgradeClanSkill(shinobi: Shinobi, skill: Skill): Shinobi? {
        if (!canUpgradeClanSkill(shinobi, skill)) return null
        val currentLevel = shinobi.clanSkills[skill.id] ?: 0
        return shinobi.copy(
            clanSkills = shinobi.clanSkills + (skill.id to (currentLevel + 1)),
            skillPoints = shinobi.skillPoints - skill.costPerLevel
        )
    }

    fun canUpgradeElementalSkill(shinobi: Shinobi, skill: Skill): Boolean {
        val currentLevel = shinobi.elementalSkills[skill.id] ?: 0
        return currentLevel < skill.maxLevel && shinobi.skillPoints >= skill.costPerLevel
    }

    fun upgradeElementalSkill(shinobi: Shinobi, skill: Skill): Shinobi? {
        if (!canUpgradeElementalSkill(shinobi, skill)) return null
        val currentLevel = shinobi.elementalSkills[skill.id] ?: 0
        return shinobi.copy(
            elementalSkills = shinobi.elementalSkills + (skill.id to (currentLevel + 1)),
            skillPoints = shinobi.skillPoints - skill.costPerLevel
        )
    }

    fun clanSkillLevel(shinobi: Shinobi, skillId: String): Int =
        shinobi.clanSkills[skillId] ?: 0

    fun elementalSkillLevel(shinobi: Shinobi, skillId: String): Int =
        shinobi.elementalSkills[skillId] ?: 0

    fun clanSkillsFor(shinobi: Shinobi): List<Skill> = ClanSkillTrees.skillsFor(shinobi.clan)
    fun elementalSkills(): List<Skill> = ElementalSkillTree.elements
}
