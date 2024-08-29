package com.sentryenterprises.sentry.enrollment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api

import com.secure.jnet.wallet.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (String) -> Unit,
) {

    val progress = nfcViewModel.fingerProgress.collectAsState().value
    val action = nfcViewModel.nfcAction.collectAsState().value
    val actionResult = nfcViewModel.nfcActionResult.collectAsState().value

    Scaffold(
        contentColor = Color.Black,
        containerColor = Color.Black,
        modifier = Modifier.background(Color.Black),
        topBar = {
            TopAppBar(
                title = {
                    Text("Fingerprint scan")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            nfcViewModel.resetNfcAction()
                            onNavigate(NAV_GET_CARD_STATE)
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

            val composition by when (action) {
                null -> rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))
                else -> rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fingerprint))
            }

            LottieAnimation(composition)

            if (action == null && actionResult == null) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    color = Color.White,
                    text = "Place your card on a flat, non-metallic surface then place a phone on top leaving sensor accessible for finger print scanning.",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            } else if (action == null && actionResult?.getOrNull() is NfcActionResult.EnrollFingerprint) {
                val resultText = when (actionResult.getOrNull()) {
                    is NfcActionResult.EnrollFingerprint.Complete -> "Enrollment complete!"
                    is NfcActionResult.EnrollFingerprint.Failed -> "Card is reporting: $progress"
                    else -> error("when do we get here? $actionResult")
                }
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    color = Color.White,
                    text = resultText,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            } else {
                val instructionText = when (progress) {
                    is BiometricProgress.Progressing -> "Remaining touches: ${progress.remainingTouches}. Lift your finger and press a slightly different part of the same finger."
                    is BiometricProgress.Feedback -> "Connecting to card ${progress.status}"
                    null -> "Connecting to card"
                }
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    color = Color.White,
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
                Button(
                    modifier = Modifier
                        .padding(start = 17.dp, end = 17.dp, bottom = 30.dp)
                        .fillMaxWidth(),
                    onClick = {
                        nfcViewModel.startNfcAction(NfcAction.EnrollFingerprint)
                    }
                ) {
                    Text("Scan Fingerprint")
                }
            } else {
                Text("Card found")
            }
        }


    }
}
