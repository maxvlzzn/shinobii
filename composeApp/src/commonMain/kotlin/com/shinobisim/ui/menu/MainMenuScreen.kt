package com.shinobisim.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.logic.GameManager
import com.shinobisim.model.SaveSummary
import com.shinobisim.ui.theme.Background
import com.shinobisim.ui.theme.LocalAppColors
import com.shinobisim.ui.theme.PrimaryButton
import com.shinobisim.ui.theme.SecondaryButton
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

    LaunchedEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SHINOBI",
            fontSize = 64.sp,
            fontWeight = FontWeight.Black,
            color = colors.accentRed,
            textAlign = TextAlign.Center
        )
        Text(
            text = "С И М У Л Я Т О Р",
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            color = colors.textSecondary,
            letterSpacing = 8.sp
        )

        Spacer(Modifier.height(64.dp))

        if (loading) {
            CircularProgressIndicator(color = colors.accentRed)
        } else {
            if (hasSave == true && saveSummary != null) {
                SecondaryButton(
                    text = "Продолжить — ${saveSummary!!.name}, ${saveSummary!!.age} лет",
                    onClick = { saveSummary?.let { onContinue(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            PrimaryButton(
                text = "Новая игра",
                onClick = onNewGame,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
