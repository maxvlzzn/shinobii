package com.shinobisim.ui.menu

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.SaveSummary
import com.shinobisim.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onContinue: (SaveSummary) -> Unit
) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    var hasSave by remember { mutableStateOf<Boolean?>(null) }
    var saveSummary by remember { mutableStateOf<SaveSummary?>(null) }
    var loading by remember { mutableStateOf(true) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        scope.launch {
            loading = true
            try {
                val manager = GameManager()
                val saves = withContext(Dispatchers.IO) { manager.listSaves() }
                hasSave = saves.isNotEmpty()
                saveSummary = saves.firstOrNull()
            } catch (e: Exception) {
                hasSave = false
                saveSummary = null
            }
            loading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(colors.darkNavy, colors.deepNavy)
                )
            )
    ) {
        ParticleBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SHINOBI",
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = colors.accentRed,
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = colors.accentRed.copy(alpha = 0.5f),
                        offset = Offset(0f, 4f),
                        blurRadius = 20f
                    )
                )
            )
            Text(
                text = "С И М У Л Я Т О Р",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = colors.textSecondary,
                letterSpacing = 8.sp
            )

            Spacer(Modifier.height(80.dp))

            if (loading) {
                CircularProgressIndicator(color = colors.accentRed)
            } else {
                ContinueButton(
                    enabled = hasSave == true,
                    saveSummary = saveSummary,
                    colors = colors,
                    onClick = {
                        saveSummary?.let { onContinue(it) }
                    }
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accentRed,
                        contentColor = colors.textPrimary
                    )
                ) {
                    Text("Новая игра", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ContinueButton(
    enabled: Boolean,
    saveSummary: SaveSummary?,
    colors: com.shinobisim.ui.theme.AppColors,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.4f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.panelNavy,
            contentColor = colors.textPrimary,
            disabledContainerColor = colors.panelNavy,
            disabledContentColor = colors.textDim
        )
    ) {
        if (saveSummary != null) {
            Text(
                "Продолжить — ${saveSummary.name}, ${saveSummary.age} лет",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            Text("Продолжить (нет сохранения)", fontSize = 16.sp)
        }
    }
}

@Composable
private fun ParticleBackground() {
    val transition = rememberInfiniteTransition()
    val particles = listOf(
        remember { ParticleSpec(0.1f, 0.2f, 0.8f, 0.9f, 3.dp) },
        remember { ParticleSpec(0.3f, 0.5f, 0.7f, 0.6f, 2.dp) },
        remember { ParticleSpec(0.5f, 0.1f, 0.6f, 0.8f, 4.dp) },
        remember { ParticleSpec(0.7f, 0.3f, 0.5f, 0.7f, 2.dp) },
        remember { ParticleSpec(0.2f, 0.7f, 0.9f, 0.5f, 3.dp) },
        remember { ParticleSpec(0.6f, 0.6f, 0.4f, 0.9f, 2.dp) },
        remember { ParticleSpec(0.8f, 0.4f, 0.3f, 0.6f, 3.dp) },
        remember { ParticleSpec(0.4f, 0.8f, 0.8f, 0.4f, 2.dp) }
    )

    particles.forEachIndexed { index, p ->
        val animProgress by transition.animateFloat(
            initialValue = p.startY,
            targetValue = p.endY,
            animationSpec = infiniteRepeatable(
                animation = tween(8000 + index * 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        val alphaAnim by transition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000 + index * 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val x = p.xRatio * size.width
            val y = animProgress * size.height
            drawCircle(
                color = Color.White.copy(alpha = alphaAnim * 0.3f),
                radius = p.size.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private data class ParticleSpec(
    val xRatio: Float,
    val startY: Float,
    val endY: Float,
    val speedFactor: Float,
    val size: androidx.compose.ui.unit.Dp
)
