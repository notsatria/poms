package com.notsatria.poms.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notsatria.poms.ui.theme.PomsTheme

@Composable
fun PomsTimer(
    modifier: Modifier = Modifier,
    progress: Float,
    timerText: String,
    strokeWidth: Float = 45f,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    progressColor: Color,
    timerTextStyle: TextStyle = TextStyle(fontSize = 48.sp)
) {
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        val canvasSize = size.minDimension
        val center = Offset(size.width / 2, size.height / 2)
        // Background circle
        drawArc(
            color = progressColor.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(canvasSize, canvasSize),
            topLeft = Offset((size.width - canvasSize) / 2, (size.height - canvasSize) / 2)
        )
        // Progress circle
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(canvasSize, canvasSize),
            topLeft = Offset((size.width - canvasSize) / 2, (size.height - canvasSize) / 2)
        )
        // Timer text
        val measuredText = textMeasurer.measure(
            AnnotatedString(timerText),
            style = timerTextStyle
        )
        val textSize = measuredText.size
        val textOffset = Offset(center.x - textSize.width / 2, center.y - textSize.height / 2)
        drawText(
            measuredText,
            topLeft = textOffset
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PomsTimerPreview() {
    PomsTheme {
        PomsTimer(progress = 0.4f, timerText = "25:00", progressColor = Color.Green)
    }
}