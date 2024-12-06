package com.sentrycard.sentry.enrollment.cardState

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api

import com.sentrycard.sentry.enrollment.R
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.sentrycard.sentry.enrollment.BuildConfig
import com.sentrycard.sentry.enrollment.NfcViewModel
import com.sentrycard.sentry.enrollment.Screen
import com.sentrycard.sentry.enrollment.SentryTheme
import com.sentrycard.sentry.enrollment.ShowStatus
import com.sentrycard.sentry.enrollment.util.PIN_BIOMETRIC
import com.sentrycard.sentry.enrollment.util.ScanStatusBottomSheet
import com.sentrycard.sentry.enrollment.util.SentryButton
import com.sentrycard.sentry.sdk.models.NfcAction
import com.sentrycard.sentry.sdk.models.NfcActionResult


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
                    Text(stringResource(R.string.get_card_status))
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.Settings) }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings)
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
                            text = stringResource(R.string.getting_started),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier.padding(bottom = 22.dp),
                            text = stringResource(R.string.place_card_on_a_flat_non_metallic_surface_place_phone_on_top_of_card_as_shown_click_scan_card_button_below),
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
                    text = stringResource(R.string.sentrycard_enroll, BuildConfig.VERSION_NAME),
                    fontSize = 11.sp,
                    color = (if (isSystemInDarkTheme()) Color.White else Color.Black).copy(alpha = .7f)
                )
            }
        }

        val context = LocalContext.current
        ScanStatusBottomSheet(
            sheetState = sheetState,
            showStatus = showStatus,
            onShowResultText = { result ->
                if (result is NfcActionResult.BiometricEnrollment) {
                    if (result.isStatusEnrollment) {
                        context.getString(R.string.not_enrolled) to context.getString(R.string.this_card_is_not_enrolled_no_fingerprints_are_recorded_on_this_card_click_ok_to_continue)
                    } else {
                        context.getString(R.string.enrolled) to context.getString(R.string.this_card_is_enrolled_a_fingerprint_is_recorded_on_this_card_click_ok_to_continue)
                    }
                } else error(context.getString(R.string.unexpected_state, showStatus))
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
            contentDescription = stringResource(R.string.place_card_here),
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
            text = stringResource(R.string.place_card_under_phone_here),
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
private fun PreviewGetCardState2Dark() {
    SentryTheme(darkTheme = true) {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Hidden,
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewScanning() {
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
            showStatus = ShowStatus.Error(IllegalStateException()),
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


@Preview(name = "Full Preview", showSystemUi = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED)
@Composable
private fun PreviewGetCardState6() {

    SentryTheme(darkTheme = true) {
        GetCardStateScreenContents(
            showStatus = ShowStatus.Result(NfcActionResult.BiometricEnrollment(false)),
            onReset = {},
            onScanClicked = {},
            onNavigate = {},
        )
    }
}
