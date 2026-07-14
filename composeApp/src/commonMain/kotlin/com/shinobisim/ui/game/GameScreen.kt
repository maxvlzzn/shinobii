package com.shinobisim.ui.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.Shinobi
import com.shinobisim.ui.theme.AccentButton
import com.shinobisim.ui.theme.Background
import com.shinobisim.ui.theme.LocalAppColors
import com.shinobisim.ui.theme.SecondaryButton
import com.shinobisim.ui.theme.toColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    initialShinobi: Shinobi,
    onOpenClanTree: (Shinobi) -> Unit,
    onOpenElementalTree: (Shinobi) -> Unit
) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var shinobi by remember { mutableStateOf(initialShinobi) }
    val manager = remember { GameManager() }

    var isSpinning by remember { mutableStateOf(false) }
    var lastSpinResult by remember { mutableStateOf<Int?>(null) }
    var showSpinResult by remember { mutableStateOf(false) }

    fun persist(updated: Shinobi) {
        shinobi = updated
        scope.launch {
            withContext(Dispatchers.IO) { manager.updateShinobi(updated) }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Left panel: character info + actions
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            ShinobiHeader(shinobi = shinobi, colors = colors)

            Spacer(Modifier.height(16.dp))

            RatingBar(rating = shinobi.rating, colors = colors)

            Spacer(Modifier.height(16.dp))

            // Action buttons
            val canIncreaseAge = !shinobi.isMaxAge

            AccentButton(
                text = if (canIncreaseAge) "Повысить возраст (+1 год)" else "Максимальный возраст",
                onClick = {
                    if (!canIncreaseAge || isSpinning) return@AccentButton
                    val aged = manager.increaseAge(shinobi)
                    if (aged != shinobi) {
                        persist(aged)
                        // Trigger roulette spin on age increase
                        isSpinning = true
                        lastSpinResult = null
                        showSpinResult = false
                        scope.launch {
                            delay(2500)
                            val (updated, points) = manager.spinRoulette(shinobi)
                            lastSpinResult = points
                            isSpinning = false
                            showSpinResult = true
                            persist(updated.copy(skillPoints = updated.skillPoints))
                        }
                    }
                },
                enabled = canIncreaseAge && !isSpinning,
                accentColor = colors.accentGreen,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = "Дерево клана",
                    onClick = { onOpenClanTree(shinobi) },
                    modifier = Modifier.weight(1f),
                    height = 48
                )
                SecondaryButton(
                    text = "Стихии",
                    onClick = { onOpenElementalTree(shinobi) },
                    modifier = Modifier.weight(1f),
                    height = 48
                )
            }

            Spacer(Modifier.height(16.dp))

            // Skill points info card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.divider, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Очки навыков", fontSize = 13.sp, color = colors.textSecondary)
                        Text(
                            "${shinobi.skillPoints}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.accentGreen
                        )
                    }
                    Text(
                        text = "Повышайте возраст,\nчтобы крутить рулетку",
                        fontSize = 12.sp,
                        color = colors.textDim,
                        textAlign = TextAlign.End,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Right panel: roulette
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showSpinResult && lastSpinResult != null) {
                SpinResultDisplay(
                    points = lastSpinResult!!,
                    colors = colors,
                    onContinue = { showSpinResult = false }
                )
            } else {
                RouletteWheel(
                    isSpinning = isSpinning,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun ShinobiHeader(shinobi: Shinobi, colors: com.shinobisim.ui.theme.AppColors) {
    val clanColor = shinobi.clan.primaryColor.toColor()
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(clanColor.copy(alpha = 0.5f), clanColor.copy(alpha = 0.1f))))
                .border(2.dp, clanColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shinobi.clan.displayName.first().toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = shinobi.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Клан ${shinobi.clan.displayName}",
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${shinobi.age} лет",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accentGold
            )
        }
    }
}

@Composable
private fun RatingBar(rating: Int, colors: com.shinobisim.ui.theme.AppColors) {
    val progress = rating.toFloat() / Shinobi.MAX_RATING
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Рейтинг", fontSize = 13.sp, color = colors.textSecondary)
            Text("$rating / ${Shinobi.MAX_RATING}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = colors.accentRed,
            trackColor = colors.panelNavy
        )
    }
}

@Composable
private fun RouletteWheel(
    isSpinning: Boolean,
    colors: com.shinobisim.ui.theme.AppColors
) {
    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val wheelNumbers = listOf(10, 1, 9, 2, 8, 3, 7, 4, 6, 5)
    val segmentAngle = 360f / wheelNumbers.size
    val textMeasurer = rememberTextMeasurer()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Рулетка навыков",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isSpinning) "Крутится..." else "Повышайте возраст, чтобы крутить",
            fontSize = 14.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f
                val currentRotation = if (isSpinning) rotation else 0f

                wheelNumbers.forEachIndexed { index, number ->
                    val startAngle = index * segmentAngle + currentRotation - 90f
                    val sweepAngle = segmentAngle

                    rotate(startAngle, center) {
                        drawArc(
                            color = if (number >= 7) colors.accentRed else if (number >= 4) colors.accentOrange else colors.panelNavy,
                            startAngle = 0f,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    val textAngle = startAngle + segmentAngle / 2f
                    val textRadius = radius * 0.7f
                    val textX = center.x + textRadius * cos(textAngle * PI / 180f).toFloat()
                    val textY = center.y + textRadius * sin(textAngle * PI / 180f).toFloat()

                    drawText(
                        textMeasurer = textMeasurer,
                        text = number.toString(),
                        topLeft = Offset(textX - 20f, textY - 20f),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                drawCircle(
                    color = colors.surface,
                    radius = radius * 0.25f,
                    center = center
                )
                drawCircle(
                    color = colors.accentGold,
                    radius = radius * 0.25f,
                    center = center,
                    style = Stroke(width = 3f)
                )
            }

            Canvas(modifier = Modifier.size(240.dp)) {
                val pointerY = 12f
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2f, pointerY + 20f)
                        lineTo(size.width / 2f - 12f, pointerY)
                        lineTo(size.width / 2f + 12f, pointerY)
                        close()
                    },
                    color = colors.accentGold
                )
            }
        }
    }
}

@Composable
private fun SpinResultDisplay(
    points: Int,
    colors: com.shinobisim.ui.theme.AppColors,
    onContinue: () -> Unit
) {
    val resultColor = if (points >= 7) colors.accentGold else if (points >= 4) colors.accentOrange else colors.accentBlue

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Выпало!",
            fontSize = 20.sp,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(resultColor, resultColor.copy(alpha = 0.2f))))
                .border(3.dp, resultColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+$points",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "очков навыков",
            fontSize = 16.sp,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(24.dp))
        AccentButton(
            text = "Продолжить",
            onClick = onContinue,
            accentColor = colors.accentGreen,
            textColor = Color.White,
            modifier = Modifier.fillMaxWidth(0.6f),
            height = 482
        )
    }
}
