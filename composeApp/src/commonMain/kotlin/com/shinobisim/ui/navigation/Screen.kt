package com.shinobisim.ui.navigation

import com.shinobisim.model.Shinobi

sealed class Screen {
    data object MainMenu : Screen()
    data object ClanSelection : Screen()
    data class Game(val shinobi: Shinobi) : Screen()
    data class ClanSkillTree(val shinobi: Shinobi) : Screen()
    data class ElementalSkillTree(val shinobi: Shinobi) : Screen()
}

object AppNavigation {
    object Routes {
        const val MAIN_MENU = "main_menu"
        const val CLAN_SELECTION = "clan_selection"
        const val GAME = "game"
        const val CLAN_SKILL_TREE = "clan_skill_tree"
        const val ELEMENTAL_SKILL_TREE = "elemental_skill_tree"
    }
}
