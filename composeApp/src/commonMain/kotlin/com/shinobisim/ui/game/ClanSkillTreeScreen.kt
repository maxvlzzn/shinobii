package com.shinobisim.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.ClanSkillTrees
import com.shinobisim.model.Shinobi
import com.shinobisim.model.Skill
import com.shinobisim.ui.theme.AccentButton
import com.shinobisim.ui.theme.Background
import com.shinobisim.ui.theme.LocalAppColors
import com.shinobisim.ui.theme.SecondaryButton
import com.shinobisim.ui.theme.toColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ClanSkillTreeScreen(
    initialShinobi: Shinobi,
    onBack: (Shinobi) -> Unit
) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var shinobi by remember { mutableStateOf(initialShinobi) }
    val manager = remember { GameManager() }
    val clanColor = shinobi.clan.primaryColor.toColor()
    val skills = remember(shinobi.clan) { ClanSkillTrees.skillsFor(shinobi.clan) }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }

    fun persist(updated: Shinobi) {
        shinobi = updated
        scope.launch {
            withContext(Dispatchers.IO) { manager.updateShinobi(updated) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                text = "Назад",
                onClick = { onBack(shinobi) },
                height = 44
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Дерево клана ${shinobi.clan.displayName}",
                    fontSize = 22.sp,
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

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(skills) { index, skill ->
                TreeSkillNode(
                    skill = skill,
                    currentLevel = shinobi.clanSkills[skill.id] ?: 0,
                    accentColor = clanColor,
                    isLast = index == skills.lastIndex,
                    onClick = { selectedSkill = skill }
                )
            }
        }
    }

    selectedSkill?.let { skill ->
        SkillDetailDialog(
            skill = skill,
            elementColor = clanColor,
            icon = skill.name.first().toString(),
            photoUrl = "",
            currentLevel = shinobi.clanSkills[skill.id] ?: 0,
            canUpgrade = manager.canUpgradeClanSkill(shinobi, skill),
            onDismiss = { selectedSkill = null },
            onConfirm = {
                manager.upgradeClanSkill(shinobi, skill)?.let { persist(it) }
                selectedSkill = null
            }
        )
    }
}

@Composable
private fun TreeSkillNode(
    skill: Skill,
    currentLevel: Int,
    accentColor: Color,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val hasLevel = currentLevel > 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .background(accentColor.copy(alpha = 0.3f))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (hasLevel) Brush.radialGradient(
                            listOf(accentColor.copy(alpha = 0.7f), accentColor.copy(alpha = 0.15f))
                        ) else Brush.radialGradient(
                            listOf(colors.panelNavy, colors.surface)
                        )
                    )
                    .border(
                        width = if (hasLevel) 3.dp else 2.dp,
                        color = if (hasLevel) accentColor else colors.divider,
                        shape = CircleShape
                    )
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = skill.name.first().toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = if (hasLevel) Color.White else colors.textSecondary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = skill.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (hasLevel) colors.textPrimary else colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Ур. $currentLevel / ${skill.maxLevel}",
            fontSize = 12.sp,
            color = colors.textDim
        )

        if (!isLast) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .background(accentColor.copy(alpha = 0.3f))
            )
        }
    }
}
