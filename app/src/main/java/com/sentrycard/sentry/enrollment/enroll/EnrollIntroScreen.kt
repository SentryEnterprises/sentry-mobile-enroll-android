package com.sentrycard.sentry.enrollment.enroll

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sentrycard.sentry.enrollment.NfcViewModel
import com.sentrycard.sentry.enrollment.R
import com.sentrycard.sentry.enrollment.Screen
import com.sentrycard.sentry.enrollment.util.SentryButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollIntroScreen(
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
                    Text("Fingerprint Enrollment")
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
            modifier = modifier
                .padding(paddingInsets)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.final_sentry_card))

            val animationState by animateLottieCompositionAsState(
                iterations = LottieConstants.IterateForever,
                restartOnPlay = true,
                composition = composition,
                isPlaying = true
            )
            LottieAnimation(
                composition = composition,
                progress = { animationState },
                modifier = Modifier.height(400.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.1f)
            )
            SentryButton(text = "Continue") {
                onNavigate(Screen.Enroll)
            }
        }
    }
}
