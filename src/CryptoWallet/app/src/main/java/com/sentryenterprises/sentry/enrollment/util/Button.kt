package com.sentryenterprises.sentry.enrollment.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SentryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .padding(start = 17.dp, end = 17.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        onClick = onClick,
    ) {
        Text(text, color = Color.White)
    }
}
