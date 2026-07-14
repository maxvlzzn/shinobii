package com.shinobisim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.Clan
import com.shinobisim.model.SaveSummary
import com.shinobisim.model.Shinobi
import com.shinobisim.ui.game.ClanSkillTreeScreen
import com.shinobisim.ui.game.ElementalSkillTreeScreen
import com.shinobisim.ui.game.GameScreen
import com.shinobisim.ui.menu.ClanSelectionScreen
import com.shinobisim.ui.menu.MainMenuScreen
import com.shinobisim.ui.theme.Background
import com.shinobisim.ui.theme.LocalAppColors
import com.shinobisim.ui.theme.SecondaryButton
import com.shinobisim.ui.theme.ShinobiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed class Route {
    data object MainMenu : Route()
    data object ClanSelection : Route()
    data class Game(val shinobi: Shinobi) : Route()
    data class ClanTree(val shinobi: Shinobi) : Route()
    data class ElementalTree(val shinobi: Shinobi) : Route()
}

@Composable
fun App() {
    ShinobiTheme {
        val colors = LocalAppColors.current
        var route by remember { mutableStateOf<Route>(Route.MainMenu) }
        val scope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Box(modifier = Modifier.fillMaxSize().background(Background)) {
            when (val current = route) {
                is Route.MainMenu -> MainMenuScreen(
                    onNewGame = { route = Route.ClanSelection },
                    onContinue = { summary: SaveSummary ->
                        scope.launch {
                            isLoading = true
                            try {
                                val manager = GameManager()
                                val loaded = withContext(Dispatchers.IO) { manager.loadSave(summary.id) }
                                if (loaded != null) {
                                    route = Route.Game(loaded)
                                }
                            } catch (e: Throwable) {
                                errorMessage = "Ошибка загрузки: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )

                is Route.ClanSelection -> ClanSelectionScreen(
                    onStart = { name, clan: Clan ->
                        scope.launch {
                            isLoading = true
                            try {
                                val manager = GameManager()
                                val created = withContext(Dispatchers.IO) { manager.startNewGame(name, clan) }
                                route = Route.Game(created)
                            } catch (e: Throwable) {
                                errorMessage = "Ошибка создания игры: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )

                is Route.Game -> GameScreen(
                    initialShinobi = current.shinobi,
                    onOpenClanTree = { route = Route.ClanTree(it) },
                    onOpenElementalTree = { route = Route.ElementalTree(it) }
                )

                is Route.ClanTree -> ClanSkillTreeScreen(
                    initialShinobi = current.shinobi,
                    onBack = { route = Route.Game(it) }
                )

                is Route.ElementalTree -> ElementalSkillTreeScreen(
                    initialShinobi = current.shinobi,
                    onBack = { route = Route.Game(it) }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Background.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.accentRed)
                }
            }

            errorMessage?.let { msg ->
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    containerColor = colors.surface,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text("Ошибка", color = colors.accentRed, fontSize = 20.sp)
                    },
                    text = {
                        Text(msg, color = colors.textPrimary, fontSize = 15.sp)
                    },
                    confirmButton = {
                        SecondaryButton(
                            text = "Закрыть",
                            onClick = { errorMessage = null },
                            height = 44
                        )
                    }
                )
            }
        }
    }
}
