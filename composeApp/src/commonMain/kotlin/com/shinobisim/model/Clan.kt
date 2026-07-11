package com.shinobisim.model

enum class Clan(val displayName: String, val description: String, val primaryColor: Long) {
    UZUMAKI(
        "Узумаки",
        "Клан longevity и печатей. Известны огромным запасом жизненной энергии.",
        0xFFFF6B35
    ),
    UCHIHA(
        "Учиха",
        "Клан Шарингана. Мастера огня и иллюзий, обладают невероятной проницательностью.",
        0xFFE63946
    ),
    HYUGA(
        "Хьюга",
        "Клан Бьякугана. Мастеры мягкого кулака и обзора на 360 градусов.",
        0xFF8ECAE6
    );

    companion object {
        val clans = entries
    }
}
