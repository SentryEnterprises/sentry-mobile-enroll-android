package com.sentrycard.sentry.enrollment.enroll

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.sentrycard.sentry.enrollment.BuildConfig
import com.sentrycard.sentry.enrollment.NfcViewModel
import com.sentrycard.sentry.enrollment.R
import com.sentrycard.sentry.enrollment.Screen
import com.sentrycard.sentry.enrollment.util.SentryButton
import com.sentrycard.sentry.enrollment.util.getDecodedMessage
import com.sentrycard.sentry.sdk.models.BiometricProgress
import com.sentrycard.sentry.sdk.models.NfcAction
import com.sentrycard.sentry.sdk.models.NfcActionResult


@Composable
fun EnrollScreen(
    nfcViewModel: NfcViewModel,
    onNavigate: (Screen) -> Unit,
) {
    val progress = nfcViewModel.fingerProgress.collectAsState().value
    val action = nfcViewModel.nfcAction.collectAsState().value
    val actionResult = nfcViewModel.nfcActionResult.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                onBack = {
                    nfcViewModel.resetNfcAction()
                    onNavigate(Screen.EnrollIntro)
                }
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .padding(top = 30.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                actionResult?.getOrNull() is NfcActionResult.EnrollFingerprint.Complete -> CheckmarkAnimation()
                progress is BiometricProgress.FingerTransition -> CheckmarkAnimation()
                action == null -> AttachCardAnimation()
                progress is BiometricProgress.Progressing -> FingerGuideImage(progress.currentStep)
                else -> FingerGuideImage(0)
            }

            if (progress is BiometricProgress.Progressing) {
                PlaySoundPerStep(progress)

                Text(
                    stringResource(R.string.finger_of_2, progress.currentFinger),
                    fontSize = 25.sp,
                    modifier = Modifier.padding(top = 30.dp)
                )
                val checkboxes =
                    (1..progress.currentStep).joinToString("") {
                        "✅"
                    } + (1..(progress.remainingTouches)).joinToString("") {
                        "◼️"
                    }
                Text(checkboxes, fontSize = 25.sp)
            }

            if (action == null && actionResult == null) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    text = stringResource(R.string.place_your_card_on_a_flat_non_metallic_surface_then_place_a_phone_on_top_leaving_sensor_accessible_for_finger_print_scanning),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            } else if (action == null && actionResult?.getOrNull() is NfcActionResult.EnrollFingerprint) {
                when (actionResult.getOrNull()) {
                    is NfcActionResult.EnrollFingerprint.Complete -> {
                        CompleteEnrollmentSection(
                            onOk = {
                                nfcViewModel.resetNfcAction()
                                onNavigate(Screen.GetCardState)
                            }
                        )
                    }

                    is NfcActionResult.EnrollFingerprint.Failed -> {
                        FailedEnrollmentSection(
                            progress = progress,
                            onRetry = {
                                nfcViewModel.resetNfcAction()
                                nfcViewModel.startNfcAction(NfcAction.EnrollFingerprint)
                            }
                        )
                    }

                    else -> error(stringResource(R.string.unexpected_state,actionResult))
                }

            } else if (action == null && actionResult?.isFailure == true) {
                UnknownFailureSection(
                    actionResult = actionResult,
                    onRetry = {
                        nfcViewModel.resetNfcAction()
                        nfcViewModel.startNfcAction(NfcAction.EnrollFingerprint)
                    }
                )
            } else {
                val instructionText = when (progress) {
                    is BiometricProgress.Progressing -> stringResource(R.string.lift_your_finger_and_press_a_slightly_different_part_of_the_same_finger)

                    is BiometricProgress.Feedback -> stringResource(
                        R.string.card_status_try_again_with_your_finger,
                        progress.status
                    )

                    is BiometricProgress.FingerTransition -> stringResource(R.string.please_use_your_second_finger)

                    null -> stringResource(R.string.press_your_finger_to_the_card_to_get_started)
                }
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    text = instructionText,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
            if (action == null && actionResult == null) {
                SentryButton(
                    modifier = Modifier.padding(bottom = 30.dp),
                    text = stringResource(R.string.scan_fingerprint),
                    onClick = {
                        nfcViewModel.startNfcAction(NfcAction.EnrollFingerprint)
                    }
                )
            }
        }
    }
}

@Composable
private fun UnknownFailureSection(
    actionResult: Result<NfcActionResult>,
    onRetry: () -> Unit,
) {
    val errorText = actionResult.exceptionOrNull().getDecodedMessage()

    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = errorText,
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = stringResource(R.string.please_move_the_phone_away_from_the_card_to_reset_then_try_again),
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    SentryButton(
        text = stringResource(R.string.retry),
        onClick = onRetry,
    )
}

@Composable
private fun CompleteEnrollmentSection(
    onOk: () -> Unit,
) {
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = stringResource(R.string.enrollment_finished),
        textAlign = TextAlign.Center,
        fontWeight = Bold,
        fontSize = 23.sp,
    )
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = stringResource(R.string.your_fingerprint_is_now_enrolled_click_ok_to_continue),
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    SentryButton(text = stringResource(R.string.ok), onClick = onOk)
}

@Composable
private fun FailedEnrollmentSection(
    progress: BiometricProgress?,
    onRetry: () -> Unit,
) {
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = stringResource(R.string.unexpected_error_occurred),
        textAlign = TextAlign.Center,
        fontWeight = Bold,
        fontSize = 23.sp,
    )
    val errorText = if (BuildConfig.DEBUG) {
        stringResource(R.string.please_try_again)
    } else {
        "$progress"
    }
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = errorText,
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    SentryButton(text = stringResource(R.string.retry), onClick = onRetry)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBar(
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(R.string.fingerprint_scan))
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
    )
}

@Composable
private fun PlaySoundPerStep(progress: BiometricProgress.Progressing) {
    val context = LocalContext.current
    LaunchedEffect(progress.currentStep) {
        if (progress.currentStep > 0) {
            val mediaPlayer = MediaPlayer.create(
                context,
                R.raw.ding
            )
            mediaPlayer.start()
        }
    }
}

@Composable
private fun FingerGuideImage(currentStep: Int) {
    val (dark, light) = when (currentStep) {
        1 -> R.drawable.finger_left_dark to R.drawable.finger_left
        2 -> R.drawable.finger_bottom_dark to R.drawable.finger_bottom
        3 -> R.drawable.finger_right_dark to R.drawable.finger_right
        4 -> R.drawable.finger_top_dark to R.drawable.finger_top
        else -> R.drawable.finger_center_dark to R.drawable.finger_center
    }
    Image(
        painter = painterResource(
            if (isSystemInDarkTheme()) {
                dark
            } else {
                light
            }
        ),
        contentDescription = stringResource(R.string.portion_of_finger_to_place),
        modifier = Modifier.size(200.dp)
    )
}

@Composable
private fun AttachCardAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))

    val animationState by animateLottieCompositionAsState(
        iterations = LottieConstants.IterateForever,
        composition = composition,
    )

    LottieAnimation(
        composition = composition,
        progress = { animationState },
        modifier = Modifier
    )
}

@Composable
private fun CheckmarkAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.checkmark_animation))

    val animationState by animateLottieCompositionAsState(
        iterations = 1,
        composition = composition,
    )

    LottieAnimation(
        composition = composition,
        progress = { animationState },
        modifier = Modifier.size(300.dp)
    )
}
