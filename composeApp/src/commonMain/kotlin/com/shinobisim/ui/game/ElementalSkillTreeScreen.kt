package com.shinobisim.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.ElementalSkillTree
import com.shinobisim.model.Shinobi
import com.shinobisim.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ElementalSkillTreeScreen(
    initialShinobi: Shinobi,
    onBack: (Shinobi) -> Unit
) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var shinobi by remember { mutableStateOf(initialShinobi) }
    val manager = remember { GameManager() }
    val elements = ElementalSkillTree.elements

    val elementColors = mapOf(
        "fire" to colors.fire,
        "water" to colors.water,
        "wind" to colors.wind,
        "lightning" to colors.lightning
    )

    fun persist(updated: Shinobi) {
        shinobi = updated
        scope.launch {
            withContext(Dispatchers.IO) { manager.updateShinobi(updated) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colors.darkNavy, colors.deepNavy)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onBack(shinobi) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.panelNavy,
                    contentColor = colors.textPrimary
                )
            ) {
                Text("Назад", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Стихийные техники",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Очки: ${shinobi.skillPoints}",
                    fontSize = 14.sp,
                    color = colors.accentGreen
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(elements) { skill ->
                val accent = elementColors[skill.id] ?: colors.accentBlue
                SkillCard(
                    skill = skill,
                    currentLevel = shinobi.elementalSkills[skill.id] ?: 0,
                    accentColor = accent,
                    canUpgrade = manager.canUpgradeElementalSkill(shinobi, skill),
                    colors = colors,
                    onUpgrade = {
                        manager.upgradeElementalSkill(shinobi, skill)?.let { persist(it) }
                    }
                )
            }
        }
    }
}
