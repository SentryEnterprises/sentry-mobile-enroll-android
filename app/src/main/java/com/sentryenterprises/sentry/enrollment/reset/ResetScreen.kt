package com.sentryenterprises.sentry.enrollment.reset

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import com.sentryenterprises.sentry.enrollment.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.sentryenterprises.sentry.enrollment.NfcViewModel
import com.sentryenterprises.sentry.enrollment.Screen
import com.sentryenterprises.sentry.enrollment.util.SentryButton
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (Screen) -> Unit,
) {

    val nfcAction = nfcViewModel.nfcAction.collectAsState().value
    val nfcActionResult = nfcViewModel.nfcActionResult.collectAsState().value
    val progress = nfcViewModel.nfcProgress.collectAsState().value
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(nfcAction) {
        if (nfcAction is NfcAction.ResetBiometricData || nfcActionResult != null) {
            sheetState.expand()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            nfcViewModel.resetNfcAction()
                            onNavigate(Screen.Settings)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text("Reset Biometric Data")
                },
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
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

            Text(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                text = "This resets the biometric fingerprint data on the card. The card will not be enrolled after this action.",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Lay the phone over the top of the card so that just the fingerprint is visible.",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )

            SentryButton(
                modifier = Modifier.padding(bottom = 30.dp),
                text = "Reset Biometric Data",
                onClick = {
                    nfcViewModel.startNfcAction(NfcAction.ResetBiometricData)
                }
            )
        }
    }

    val scope = rememberCoroutineScope()
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }
            },
            sheetState = sheetState,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (nfcActionResult?.getOrNull() != null && nfcActionResult.getOrNull() is NfcActionResult.ResetBiometrics) {

                    Text(
                        modifier = Modifier.padding(bottom = 25.dp),
                        text = "Reset Result",
                        fontSize = 23.sp,
                        fontWeight = Bold,
                    )

                    val resultText = when (nfcActionResult.getOrNull()) {
                        is NfcActionResult.ResetBiometrics.Success -> {
                            "The reset was successful, this card is no longer enrolled."
                        }

                        is NfcActionResult.ResetBiometrics.Failed -> {
                            "An error occurred. Please try again. (${nfcActionResult})"
                        }

                        else -> "Unexpected state: $nfcActionResult"
                    }

                    Text(
                        modifier = Modifier.padding(bottom = 50.dp),
                        textAlign = TextAlign.Center,
                        text = resultText,
                    )

                } else {

                    val (statusText, isProgressing) = if (progress == null && nfcAction is NfcAction.ResetBiometricData) {
                        "Scanning" to true
                    } else if (progress != null) {
                        "Card found" to true
                    } else {
                        "Error $nfcActionResult" to false
                    }
                    if (isProgressing) {
                        CircularProgressIndicator()
                    }

                    Text(
                        modifier = Modifier.padding(bottom = 25.dp),
                        text = statusText,
                        fontSize = 23.sp,
                        fontWeight = Bold,
                    )

                    val coroutine = rememberCoroutineScope()
                    SentryButton(
                        modifier = Modifier.padding(bottom = 30.dp),
                        text = "Cancel"
                    ) {
                        coroutine.launch {
                            sheetState.hide()
                        }
                        nfcViewModel.resetNfcAction()
                    }
                }
            }
        }
    }
}