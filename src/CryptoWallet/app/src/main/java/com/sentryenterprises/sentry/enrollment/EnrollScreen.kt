package com.sentryenterprises.sentry.enrollment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.sentryenterprises.sentry.enrollment.util.SentryButton
import com.sentryenterprises.sentry.sdk.apdu.getDecodedMessage
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (Screen) -> Unit,
) {

    val progress = nfcViewModel.fingerProgress.collectAsState().value
    val action = nfcViewModel.nfcAction.collectAsState().value
    val actionResult = nfcViewModel.nfcActionResult.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Fingerprint scan")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            nfcViewModel.resetNfcAction()
                            onNavigate(Screen.GetCardState)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
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

            LaunchedEffect(progress) {
                println("EnrollScreen progress $progress")
            }
            LaunchedEffect(action) {
                println("EnrollScreen progress $action")
            }
            LaunchedEffect(actionResult) {
                println("EnrollScreen actionResult $actionResult")
            }

            val composition by when (action) {
                null -> rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))
                else -> rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fingerprint))
            }

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
            if (progress is BiometricProgress.Progressing) {
                val checkboxes = (1..progress.currentStep).map {
                    "✅"
                }.joinToString("") +
                        (1..(progress.remainingTouches)).map {
                            "◼️"
                        }.joinToString("")
                Text(checkboxes, fontSize = 25.sp)
            }

            if (action == null && actionResult == null) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    text = "Place your card on a flat, non-metallic surface then place a phone on top leaving sensor accessible for finger print scanning.",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            } else if (action == null && actionResult?.getOrNull() is NfcActionResult.EnrollFingerprint) {
                when (actionResult.getOrNull()) {
                    is NfcActionResult.EnrollFingerprint.Complete -> {
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
                        SentryButton(text = "Ok") {
                            nfcViewModel.resetNfcAction()
                            onNavigate(Screen.GetCardState)
                        }
                    }

                    is NfcActionResult.EnrollFingerprint.Failed -> {
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
                        SentryButton(text = "Retry") {
                            nfcViewModel.resetNfcAction()
                            nfcViewModel.startNfcAction(NfcAction.EnrollFingerprint)
                        }
                    }

                    else -> error("Unexpected state: $actionResult")
                }

            } else if (action == null && actionResult?.isFailure == true) {

                val errorText = actionResult.exceptionOrNull().getDecodedMessage()

                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    text = errorText,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    text = "Please move the phone away from the card and try again.",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
                SentryButton(
                    text = "Retry",
                    onClick = {
                        nfcViewModel.resetNfcAction()
                        nfcViewModel.startNfcAction(NfcAction.EnrollFingerprint)
                    }
                )
            } else {
                val instructionText = when (progress) {
                    is BiometricProgress.Progressing -> "Remaining touches: ${progress.remainingTouches}. Lift your finger and press a slightly different part of the same finger."
                    is BiometricProgress.Feedback -> "Card status:${progress.status}. Try again with your finger."
                    is BiometricProgress.FingerTransition -> "Please use your second finger"
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
