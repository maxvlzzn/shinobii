package com.shinobisim.model

import kotlinx.serialization.Serializable

@Serializable
data class SkillTree(
    val skills: List<Skill>
)

@Serializable
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val maxLevel: Int = 5,
    val costPerLevel: Int = 1
)

object ClanSkillTrees {

    val uzumakiSkills = listOf(
        Skill("uzumaki_seals", "Печати Узумаки", "Мастерство фуиндзюцу. Усиливает запечатывающие техники.", 5, 1),
        Skill("uzumaki_vitality", "Жизненная сила", "Огромный запас чакры и выносливость клана Узумаки.", 5, 1),
        Skill("uzumaki_healing", "Техника исцеления", "Способность быстро восстанавливать раны.", 5, 2),
        Skill("uzumaki_chakra_chains", "Цепи чакры", "Уникальная техника Адама-Напрягающих Цепей.", 3, 3)
    )

    val uchihaSkills = listOf(
        Skill("uchiha_sharingan", "Шаринган", "Глаз копирует техники и предугадывает движения врага.", 5, 1),
        Skill("uchiha_fireball", "Техника Огненного Шара", "Классическая техника огня клана Учиха.", 5, 1),
        Skill("uchiha_genjutsu", "Гендзюцу", "Иллюзии, воздействующие на разум противника.", 5, 2),
        Skill("uchiha_mangekyou", "Мангекё Шаринган", "Продвинутая форма Шарингана с уникальными техниками.", 3, 3)
    )

    val hyugaSkills = listOf(
        Skill("hyuga_byakugan", "Бьякуган", "Обзор на 360 градусов и видение тенкецу (точек чакры).", 5, 1),
        Skill("hyuga_gentle_fist", "Мягкий кулак", "Удары, блокирующие потоки чакры противника.", 5, 1),
        Skill("hyuga_rotation", "Небесное Вращение", "Защитная техника отбивания любых атак.", 5, 2),
        Skill("hyuga_64_palms", "64 Удара Небес", "Серия ударов по 64 точкам чакры, полностью обездвиживает.", 3, 3)
    )

    fun skillsFor(clan: Clan): List<Skill> = when (clan) {
        Clan.UZUMAKI -> uzumakiSkills
        Clan.UCHIHA -> uchihaSkills
        Clan.HYUGA -> hyugaSkills
    }
}

object ElementalSkillTree {

    val elements = listOf(
        Skill("fire", "Стихия Огня", "Техники огня: огненные шары, стены пламени, метеоры.", 5, 1),
        Skill("water", "Стихия Воды", "Техники воды: водяные драконы, стены воды, акулы.", 5, 1),
        Skill("wind", "Стихия Ветра", "Техники ветра: воздушные лезвия, вихри, бури.", 5, 1),
        Skill("lightning", "Стихия Молнии", "Техники молнии: чидори, броня молнии, чёрная искра.", 5, 1)
    )
}
