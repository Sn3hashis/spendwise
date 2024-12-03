package me.sm.spendwise.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun Modifier.blurredBackground(
    color: Color,
    borderRadius: Dp,
    blurRadius: Dp
) = this.drawBehind {
    drawRect(color)
} 