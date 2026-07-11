package com.shinobisim.ui.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.ClanSkillTrees
import com.shinobisim.model.Shinobi
import com.shinobisim.model.Skill
import com.shinobisim.ui.theme.LocalAppColors
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
                    text = "Дерево клана ${shinobi.clan.displayName}",
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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(skills) { skill ->
                SkillCard(
                    skill = skill,
                    currentLevel = shinobi.clanSkills[skill.id] ?: 0,
                    accentColor = clanColor,
                    canUpgrade = manager.canUpgradeClanSkill(shinobi, skill),
                    colors = colors,
                    onUpgrade = {
                        manager.upgradeClanSkill(shinobi, skill)?.let { persist(it) }
                    }
                )
            }
        }
    }
}

@Composable
fun SkillCard(
    skill: Skill,
    currentLevel: Int,
    accentColor: androidx.compose.ui.graphics.Color,
    canUpgrade: Boolean,
    colors: com.shinobisim.ui.theme.AppColors,
    onUpgrade: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (canUpgrade) accentColor.copy(alpha = 0.12f) else colors.panelNavy,
        animationSpec = tween(300),
        label = "skill_bg"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, colors.divider, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(accentColor.copy(alpha = 0.5f), accentColor.copy(alpha = 0.1f))))
                    .border(2.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = skill.name.first().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = skill.description,
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        LevelBar(
            currentLevel = currentLevel,
            maxLevel = skill.maxLevel,
            accentColor = accentColor,
            colors = colors
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Стоимость: ${skill.costPerLevel} оч.",
                fontSize = 13.sp,
                color = colors.textSecondary
            )
            Button(
                onClick = onUpgrade,
                enabled = canUpgrade,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = colors.textPrimary,
                    disabledContainerColor = colors.panelNavy,
                    disabledContentColor = colors.textDim
                )
            ) {
                Text(
                    text = if (currentLevel >= skill.maxLevel) "Максимум" else "Прокачать",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LevelBar(
    currentLevel: Int,
    maxLevel: Int,
    accentColor: androidx.compose.ui.graphics.Color,
    colors: com.shinobisim.ui.theme.AppColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxLevel) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < currentLevel) accentColor else colors.divider)
            )
        }
    }
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Уровень $currentLevel / $maxLevel",
        fontSize = 12.sp,
        color = colors.textSecondary
    )
}
