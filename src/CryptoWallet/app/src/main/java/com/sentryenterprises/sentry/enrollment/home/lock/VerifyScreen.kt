package com.sentryenterprises.sentry.enrollment.home.lock

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentryenterprises.sentry.enrollment.NfcViewModel
import com.sentryenterprises.sentry.enrollment.Screen
import com.sentryenterprises.sentry.enrollment.ShowStatus
import com.sentryenterprises.sentry.enrollment.util.ScanStatusBottomSheet
import com.sentryenterprises.sentry.enrollment.util.SentryButton
import com.sentryenterprises.sentry.sdk.models.FingerprintValidation
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (Screen) -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showStatus = nfcViewModel.showStatus.collectAsState(ShowStatus.Hidden).value

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
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigate(Screen.GetCardState)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text("Verify Fingerprint")
                },
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (
                showStatus is ShowStatus.Result &&
                showStatus.result is NfcActionResult.VerifyBiometric &&
                showStatus.result.fingerprintValidation == FingerprintValidation.MatchValid
            ) {
                Unlocked(modifier = Modifier.size(400.dp))
            } else {
                Locked(modifier = Modifier.size(400.dp))
            }


//            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))
//
//            val animationState by animateLottieCompositionAsState(
//                iterations = LottieConstants.IterateForever,
//                restartOnPlay = true,
//                composition = composition,
//                isPlaying = true
//            )
//            LottieAnimation(
//                composition = composition,
//                progress = { animationState },
//                modifier = modifier
//            )

            Text(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                text = "Place your card on a flat, non-metallic surface then place a phone on top leaving sensor accessible for finger print scanning.",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            SentryButton(
                modifier = Modifier.padding(bottom = 30.dp),
                text = "Verify Fingerprint",
                onClick = {
                    nfcViewModel.startNfcAction(NfcAction.VerifyBiometric)
                }
            )
        }

        ScanStatusBottomSheet(
            sheetState = sheetState,
            showStatus = showStatus,
            cardFoundText = "Place your finger on the card.",
            onShowResultText = { result ->
                if (result is NfcActionResult.VerifyBiometric) {
                    "Verification Status" to when (result.fingerprintValidation) {
                        FingerprintValidation.MatchValid -> {
                            "Fingerprint successfully verified!"
                        }

                        FingerprintValidation.MatchFailed -> {
                            "Fingerprint did not match."
                        }

                        else -> {
                            "This card is not enrolled."
                        }
                    }
                } else error("Unexpected state $showStatus")
            },
            onButtonClicked = {
                nfcViewModel.resetNfcAction()
            },
            onDismiss = {
                nfcViewModel.resetNfcAction()
            }
        )

    }
}

