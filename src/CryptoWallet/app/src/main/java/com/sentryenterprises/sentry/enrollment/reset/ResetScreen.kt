package com.sentryenterprises.sentry.enrollment.reset

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import com.sentryenterprises.sentry.enrollment.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import com.sentryenterprises.sentry.enrollment.NAV_SETTINGS
import com.sentryenterprises.sentry.enrollment.NfcViewModel
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (String) -> Unit,
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
        contentColor = Color.Black,
        containerColor = Color.Black,
        modifier = Modifier.background(Color.Black),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            nfcViewModel.resetNfcAction()
                            onNavigate(NAV_SETTINGS)
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
            LottieAnimation(composition)

            Text(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                color = Color.White,
                text = "This resets the biometric fingerprint data on the card. The card will not be enrolled after this action.",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.White,
                text = "Lay the phone over the top of the card so that just the fingerprint is visible.",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )

            Button(
                modifier = Modifier
                    .padding(start = 17.dp, end = 17.dp, bottom = 30.dp)
                    .fillMaxWidth(),
                onClick = {
                    nfcViewModel.startNfcAction(NfcAction.ResetBiometricData)
                }
            ) {
                Text("Reset Biometric Data")
            }
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
            if (nfcActionResult != null && nfcActionResult is NfcActionResult.ResetBiometrics) {

                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 25.dp),
                    text = "Reset Result",
                    color = Color.White,
                    fontWeight = Bold,
                )

                val resultText = when (nfcActionResult) {
                    is NfcActionResult.ResetBiometrics.Success -> {
                        "The reset was successful, this card is no longer enrolled."
                    }

                    is NfcActionResult.ResetBiometrics.Failed -> {
                        "An error occurred. Please try again. (${nfcActionResult.reason})"
                    }
                }

                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 50.dp),
                    text = resultText,
                    color = Color.White,
                )

            } else {

                val (statusText, isProgressing) = if (progress == null && nfcAction is NfcAction.ResetBiometricData) {
                    "Scanning" to true
                } else if (progress != null){
                    "Card found" to true
                } else {
                    "Error" to false
                }
                if (isProgressing) {
                    CircularProgressIndicator(color = Color.White)
                }

                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 25.dp),
                    text = statusText,
                    color = Color.White,
                    fontWeight = Bold,
                )
            }
        }
    }
}