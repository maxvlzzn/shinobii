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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.ElementalSkillTree
import com.shinobisim.model.Shinobi
import com.shinobisim.model.Skill
import com.shinobisim.ui.theme.AccentButton
import com.shinobisim.ui.theme.Background
import com.shinobisim.ui.theme.LocalAppColors
import com.shinobisim.ui.theme.SecondaryButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class ElementInfo(
    val skill: Skill,
    val color: androidx.compose.ui.graphics.Color,
    val icon: String,
    val photoUrl: String
)

@Composable
fun ElementalSkillTreeScreen(
    initialShinobi: Shinobi,
    onBack: (Shinobi) -> Unit
) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var shinobi by remember { mutableStateOf(initialShinobi) }
    val manager = remember { GameManager() }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }

    val elements = remember {
        listOf(
            ElementInfo(
                ElementalSkillTree.elements[0],
                colors.fire,
                "\uD83D\uDD25",
                "https://images.pexels.com/photos/9803810/pexels-photo-9803810.jpeg"
            ),
            ElementInfo(
                ElementalSkillTree.elements[1],
                colors.water,
                "\uD83D\uDCA7",
                "https://images.pexels.com/photos/189349/pexels-photo-189349.jpeg"
            ),
            ElementInfo(
                ElementalSkillTree.elements[2],
                colors.wind,
                "\uD83C\uDF2A\uFE0F",
                "https://images.pexels.com/photos/1671325/pexels-photo-1671325.jpeg"
            ),
            ElementInfo(
                ElementalSkillTree.elements[3],
                colors.lightning,
                "\u26A1",
                "https://images.pexels.com/photos/2387873/pexels-photo-2387873.jpeg"
            )
        )
    }

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
                    text = "Стихийные техники",
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

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Выберите стихию",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            elements.forEach { element ->
                ElementCircle(
                    info = element,
                    level = shinobi.elementalSkills[element.skill.id] ?: 0,
                    onClick = { selectedSkill = element.skill }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(elements) { element ->
                ElementLevelBar(
                    info = element,
                    currentLevel = shinobi.elementalSkills[element.skill.id] ?: 0,
                    onClick = { selectedSkill = element.skill }
                )
            }
        }
    }

    selectedSkill?.let { skill ->
        val element = elements.find { it.skill.id == skill.id }
        SkillDetailDialog(
            skill = skill,
            elementColor = element?.color ?: colors.accentBlue,
            icon = element?.icon ?: "",
            photoUrl = element?.photoUrl ?: "",
            currentLevel = shinobi.elementalSkills[skill.id] ?: 0,
            canUpgrade = manager.canUpgradeElementalSkill(shinobi, skill),
            onDismiss = { selectedSkill = null },
            onConfirm = {
                manager.upgradeElementalSkill(shinobi, skill)?.let { persist(it) }
                selectedSkill = null
            }
        )
    }
}

@Composable
private fun ElementCircle(
    info: ElementInfo,
    level: Int,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val hasLevel = level > 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (hasLevel) Brush.radialGradient(
                        listOf(info.color.copy(alpha = 0.8f), info.color.copy(alpha = 0.2f))
                    ) else Brush.radialGradient(
                        listOf(colors.panelNavy, colors.surface)
                    )
                )
                .border(
                    width = if (hasLevel) 3.dp else 2.dp,
                    color = if (hasLevel) info.color else colors.divider,
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = info.icon,
                fontSize = 32.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = info.skill.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (hasLevel) colors.textPrimary else colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Ур. $level/${info.skill.maxLevel}",
            fontSize = 11.sp,
            color = colors.textDim
        )
    }
}

@Composable
private fun ElementLevelBar(
    info: ElementInfo,
    currentLevel: Int,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = info.skill.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$currentLevel / ${info.skill.maxLevel}",
                fontSize = 14.sp,
                color = colors.textSecondary
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(info.skill.maxLevel) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (index < currentLevel) info.color else colors.divider)
                )
            }
        }
    }
}

@Composable
fun SkillDetailDialog(
    skill: Skill,
    elementColor: androidx.compose.ui.graphics.Color,
    icon: String,
    photoUrl: String,
    currentLevel: Int,
    canUpgrade: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        containerColor = colors.surface,
        shape = RoundedCornerShape(20.dp),
        title = null,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(elementColor.copy(alpha = 0.6f), elementColor.copy(alpha = 0.1f))
                            )
                        )
                        .border(3.dp, elementColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 48.sp)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = skill.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = skill.description,
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Уровень", fontSize = 12.sp, color = colors.textDim)
                        Text(
                            "$currentLevel / ${skill.maxLevel}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Стоимость", fontSize = 12.sp, color = colors.textDim)
                        Text(
                            "${skill.costPerLevel} оч.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentGreen
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                AccentButton(
                    text = if (currentLevel >= skill.maxLevel) "Максимум" else "Изучить технику",
                    onClick = onConfirm,
                    enabled = canUpgrade,
                    accentColor = elementColor,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                SecondaryButton(
                    text = "Закрыть",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    height = 44
                )
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
