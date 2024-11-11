package com.sentryenterprises.sentry.enrollment.cardState

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api

import com.sentryenterprises.sentry.enrollment.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.model.animatable.AnimatableColorValue
import com.sentryenterprises.sentry.enrollment.BuildConfig
import com.sentryenterprises.sentry.enrollment.NfcViewModel
import com.sentryenterprises.sentry.enrollment.Screen
import com.sentryenterprises.sentry.enrollment.SentryTheme
import com.sentryenterprises.sentry.enrollment.ShowStatus
import com.sentryenterprises.sentry.enrollment.util.PIN_BIOMETRIC
import com.sentryenterprises.sentry.enrollment.util.ScanStatusBottomSheet
import com.sentryenterprises.sentry.enrollment.util.SentryButton
import com.sentryenterprises.sentry.sdk.models.FingerprintValidation
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetCardStateScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (Screen) -> Unit,
) {

    val showStatus = nfcViewModel.showStatus.collectAsState(ShowStatus.Hidden).value
    GetCardStateScreenContents(
        modifier = modifier,
        showStatus = showStatus,
        onScanClicked = {
            nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
        },
        onReset = {
            nfcViewModel.resetNfcAction()
        },
        onNavigate = onNavigate,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetCardStateScreenContents(
    modifier: Modifier = Modifier,
    showStatus: ShowStatus,
    onReset: () -> Unit,
    onScanClicked: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(showStatus) {
        when (showStatus) {
            is ShowStatus.Hidden -> sheetState.hide()

            is ShowStatus.CardFound,
            is ShowStatus.Scanning,
            is ShowStatus.Error,
            is ShowStatus.Result -> sheetState.expand()
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Get Card Status")
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.Settings) }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingInsets ->
        Box {
            if (sheetState.isVisible) {
                PlaceCardHere()
            }
            Column(
                modifier = Modifier
                    .padding(paddingInsets)
                    .padding(top = 80.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                if (sheetState.isVisible) {
//                    Spacer(Modifier.height(200.dp))
//                }
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))

                val animationState by animateLottieCompositionAsState(
                    iterations = LottieConstants.IterateForever,
                    restartOnPlay = true,
                    composition = composition,
                    isPlaying = true
                )
                LottieAnimation(
                    composition = composition,
                    progress = { animationState },
                    modifier = modifier
                )
                LazyColumn(Modifier.weight(1f)) {
                    item {
                        Text(
                            modifier = Modifier
                                .padding(top = 32.dp, bottom = 10.dp)
                                .fillMaxWidth(),
                            text = "Getting Started:",
//                            text = "Place your card on a flat, non-metallic surface then place a phone on top leaving sensor accessible for finger print scanning.",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier.padding(bottom = 22.dp),
                            text = "• Place card on a flat, non-metallic surface\n• Place phone on top of card as shown\n• Click \"Scan Card\" button below",
                            fontSize = 17.sp,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(.1f)
                )
                SentryButton(
                    text = "Scan Card",
                    onClick = onScanClicked
                )
                Text(
                    modifier = Modifier.padding(bottom = 30.dp),
                    text = "SentryCard Enroll \uD83D\uDD12 ${BuildConfig.VERSION_NAME}",
                    fontSize = 11.sp,
                    color = Color.Unspecified.copy(alpha = .7f)
                )
            }
        }

        ScanStatusBottomSheet(
            sheetState = sheetState,
            showStatus = showStatus,
            onShowResultText = { result ->
                if (result is NfcActionResult.BiometricEnrollment) {
                    if (result.isStatusEnrollment) {
                        "Not Enrolled" to "This card is not enrolled. No fingerprints are recorded on this card. Click OK to continue."
                    } else {
                        "Enrolled" to "This card is enrolled. A fingerprint is recorded on this card. Click OK to continue."
                    }
                } else error("Unexpected state $showStatus")
            },
            onButtonClicked = {
                if (showStatus is ShowStatus.Result && showStatus.result is NfcActionResult.BiometricEnrollment) {
                    if (showStatus.result.isStatusEnrollment) {
                        onNavigate(Screen.EnrollIntro)
                    } else {
                        onNavigate(Screen.Verify)
                    }

                } else {
                    println("unexpected: showStatus $showStatus")
                }
                onReset()
            },
            onDismiss = {
                onReset()
            }
        )
    }
}

@Composable
private fun PlaceCardHere() {
    val borderColor = remember { Animatable(Color.Black) }
    LaunchedEffect(Unit) {
        borderColor.animateTo(
            Color.White,
            animationSpec = infiniteRepeatable(tween(500), repeatMode = RepeatMode.Reverse)
        )
    }
    Box(
        Modifier
    ) {
        Image(
            painter = painterResource(
                if (isSystemInDarkTheme()) {
                    R.drawable.card_white
                } else {
                    R.drawable.card_black
                }
            ),
            contentDescription = "Place card here",
            modifier = Modifier
                .offset((100).dp, (120).dp)
                .scale(1.4f)
                .alpha(.5f)
        )

        val textMeasurer = rememberTextMeasurer()
        val textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.W400,
        )
        val textLayout = textMeasurer.measure(
            text = "Place Card Under Phone Here",
            maxLines = 1,
            style = textStyle,
        )
        Canvas(
            Modifier
                .width(500.dp)
                .height(335.dp)
                .offset(26.dp, 75.dp)
        ) {

            drawRoundRect(
                color = borderColor.value,
                cornerRadius = CornerRadius(50f, 50f),
                style = Stroke(5f)
            )
            drawText(
                topLeft = Offset(80f, size.height - textLayout.size.height - 30f),
                textLayoutResult = textLayout
            )

        }
    }
}

@Preview
@Composable
private fun PreviewGetCardState() {
    SentryTheme {
        GetCardStateScreenContents(
            showStatus = ShowStatus.CardFound,
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}

@Preview
@Composable
private fun PreviewGetCardState2() {
    SentryTheme {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Hidden,
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}

@Preview
@Composable
private fun PreviewGetCardState3() {
    SentryTheme {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Scanning,
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}

@Preview
@Composable
private fun PreviewGetCardState4() {
    SentryTheme {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Error("Error"),
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}


@Preview
@Composable
private fun PreviewGetCardState5() {
    SentryTheme {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Result(NfcActionResult.BiometricEnrollment(true)),
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}


@Preview(name = "Light Mode",)
@Preview(name = "Full Preview", showSystemUi = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED, showBackground = true)
@Composable
private fun PreviewGetCardState6() {

    SentryTheme {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Result(NfcActionResult.BiometricEnrollment(false)),
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}
