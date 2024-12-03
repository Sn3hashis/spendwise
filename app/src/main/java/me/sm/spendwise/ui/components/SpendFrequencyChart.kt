package me.sm.spendwise.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb

@Composable
fun SpendFrequencyChart() {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val points = listOf(0.2f, 0.5f, 0.3f, 0.8f, 0.4f, 0.7f, 0.5f)
        
        points.forEachIndexed { index, point ->
            if (index < points.size - 1) {
                drawLine(
                    start = Offset(
                        x = width * (index.toFloat() / (points.size - 1)),
                        y = height * (1 - points[index])
                    ),
                    end = Offset(
                        x = width * ((index + 1).toFloat() / (points.size - 1)),
                        y = height * (1 - points[index + 1])
                    ),
                    color = primaryColor,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
} 