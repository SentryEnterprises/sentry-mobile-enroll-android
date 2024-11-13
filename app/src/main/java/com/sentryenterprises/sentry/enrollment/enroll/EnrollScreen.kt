package com.sentryenterprises.sentry.enrollment.enroll

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.sentryenterprises.sentry.enrollment.BuildConfig
import com.sentryenterprises.sentry.enrollment.NfcViewModel
import com.sentryenterprises.sentry.enrollment.R
import com.sentryenterprises.sentry.enrollment.Screen
import com.sentryenterprises.sentry.enrollment.util.SentryButton
import com.sentryenterprises.sentry.sdk.apdu.getDecodedMessage
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult


@OptIn(ExperimentalMaterial3Api::class)
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
                    {
                        nfcViewModel.resetNfcAction()
                        onNavigate(Screen.EnrollIntro)
                    }
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
                    "Finger ${progress.currentFinger} of 2\n",
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
                    text = "Place your card on a flat, non-metallic surface then place a phone " +
                            "on top leaving sensor accessible for finger print scanning.",
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

                    else -> error("Unexpected state: $actionResult")
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
                    is BiometricProgress.Progressing -> "Lift your finger and press a " +
                            "slightly different part of the same finger."

                    is BiometricProgress.Feedback -> "Card status:${progress.status}. " +
                            "Try again with your finger."

                    is BiometricProgress.FingerTransition -> "Please use your second " +
                            "finger."

                    null -> "Press your finger to the card to get started."
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
                    text = "Scan Fingerprint",
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
        text = "Please move the phone away from the card to reset, then try again.",
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    SentryButton(
        text = "Retry",
        onClick = onRetry,
    )
}

@Composable
private fun CompleteEnrollmentSection(
    onOk: () -> Unit,
) {
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = "Enrollment Finished",
        textAlign = TextAlign.Center,
        fontWeight = Bold,
        fontSize = 23.sp,
    )
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = "Your fingerprint is now enrolled. Click Ok to continue.",
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    SentryButton(text = "Ok", onClick = onOk)
}

@Composable
private fun FailedEnrollmentSection(
    progress: BiometricProgress?,
    onRetry: () -> Unit,
) {
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = "Unexpected error occurred",
        textAlign = TextAlign.Center,
        fontWeight = Bold,
        fontSize = 23.sp,
    )
    val errorText = if (BuildConfig.DEBUG) {
        "Please try again."
    } else {
        "$progress"
    }
    Text(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        text = errorText,
        textAlign = TextAlign.Center,
        fontSize = 17.sp
    )
    SentryButton(text = "Retry", onClick = onRetry)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBar(
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text("Fingerprint scan")
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
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
        contentDescription = "Portion of finger to place",
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
