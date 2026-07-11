package com.shinobisim.ui.menu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shinobisim.model.Clan
import com.shinobisim.ui.theme.LocalAppColors
import com.shinobisim.ui.theme.toColor

@Composable
fun ClanSelectionScreen(
    onStart: (String, Clan) -> Unit
) {
    val colors = LocalAppColors.current
    var name by remember { mutableStateOf("") }
    var selectedClan by remember { mutableStateOf<Clan?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colors.darkNavy, colors.deepNavy)))
            .padding(24.dp)
    ) {
        Text(
            text = "Создание синоби",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Введите имя и выберите клан",
            fontSize = 16.sp,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя синоби") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.panelNavy,
                unfocusedContainerColor = colors.panelNavy,
                focusedIndicatorColor = colors.accentRed,
                unfocusedIndicatorColor = colors.divider,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedLabelColor = colors.accentRed,
                unfocusedLabelColor = colors.textSecondary
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Выберите клан",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(Clan.clans) { clan ->
                ClanCard(
                    clan = clan,
                    selected = selectedClan == clan,
                    onClick = { selectedClan = clan }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val clan = selectedClan
                if (name.isNotBlank() && clan != null) {
                    onStart(name.trim(), clan)
                }
            },
            enabled = name.isNotBlank() && selectedClan != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accentRed,
                contentColor = colors.textPrimary,
                disabledContainerColor = colors.panelNavy,
                disabledContentColor = colors.textDim
            )
        ) {
            Text("Начать путь синоби", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ClanCard(
    clan: Clan,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val clanColor = clan.primaryColor.toColor()
    val borderColor by animateColorAsState(
        targetValue = if (selected) clanColor else colors.divider,
        animationSpec = tween(300),
        label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) clanColor.copy(alpha = 0.15f) else colors.panelNavy,
        animationSpec = tween(300),
        label = "bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(clanColor.copy(alpha = 0.6f), clanColor.copy(alpha = 0.2f))
                    )
                )
                .border(2.dp, clanColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = clan.displayName.first().toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = clan.displayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = clan.description,
                fontSize = 13.sp,
                color = colors.textSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
