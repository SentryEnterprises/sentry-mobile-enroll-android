package com.sentryenterprises.sentry.enrollment.home.lock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
private fun Prev() {

    Column {
        Locked(
            Modifier
                .size(200.dp)
        )
        Unlocked(
            Modifier
                .size(200.dp)
        )
    }

}

@Composable
fun Unlocked(modifier: Modifier = Modifier) {
    val green = Color(0xFF228B22)
    Canvas(
        modifier = modifier
    ) {
        val corner = size.width * .1f
        val bodySize = size.times(.5f)
        drawRoundRect(
            color = green,
            topLeft = Offset(center.x - bodySize.width / 2f, center.y),
            size = bodySize,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawPath(
            Path().apply {
                val arcCenterX = center.x + size.width * .29f
                val arcCenterY = center.y - size.height * .1f
                moveTo(arcCenterX - size.width * .15f, arcCenterY + size.height * .15f)
                lineTo(arcCenterX - size.width * .15f, arcCenterY)
                arcTo(
                    Rect(
                        topLeft = Offset(
                            arcCenterX - size.width * .15f,
                            arcCenterY - size.height * .15f
                        ),
                        bottomRight = Offset(
                            arcCenterX + size.width * .15f,
                            arcCenterY + size.height * .15f
                        )
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(arcCenterX + size.width * .15f, arcCenterY + size.height * .15f)
            },
            color = green,
            style = Stroke(width = size.width * .08f)
        )
    }
}

@Composable
fun Locked(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
    ) {
        val corner = size.width * .1f
        val bodySize = size.times(.5f)
        drawRoundRect(
            Color.Red,
            topLeft = Offset(center.x - bodySize.width / 2f, center.y),
            size = bodySize,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawPath(
            Path().apply {
                val arcCenterX = center.x
                val arcCenterY = center.y - size.height * .1f
                moveTo(arcCenterX - size.width * .15f, arcCenterY + size.height * .15f)
                lineTo(arcCenterX - size.width * .15f, arcCenterY)
                arcTo(
                    Rect(
                        topLeft = Offset(
                            arcCenterX - size.width * .15f,
                            arcCenterY - size.height * .15f
                        ),
                        bottomRight = Offset(
                            arcCenterX + size.width * .15f,
                            arcCenterY + size.height * .15f
                        )
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(center.x + size.width * .15f, arcCenterY + size.height * .15f)
            },
            Color.Red,
            style = Stroke(width = size.width * .08f)
        )
    }
}