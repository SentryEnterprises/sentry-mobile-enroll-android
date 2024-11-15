package com.sentrycard.sentry.enrollment.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        shape = RoundedCornerShape(15.dp),
        onClick = onClick,
    ) {
        Text(text, color = Color.White, fontWeight = W600, fontSize = 17.sp, modifier = Modifier.padding(vertical = 10.dp))
    }
}
