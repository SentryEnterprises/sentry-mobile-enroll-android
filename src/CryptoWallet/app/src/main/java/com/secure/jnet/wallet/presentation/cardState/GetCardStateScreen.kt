package com.secure.jnet.wallet.presentation.cardState

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api

import com.secure.jnet.wallet.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
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
import com.secure.jnet.wallet.presentation.NAV_ENROLL
import com.secure.jnet.wallet.presentation.NAV_LOCK
import com.secure.jnet.wallet.presentation.NAV_SETTINGS
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.presentation.ShowStatus
import com.secure.jnet.wallet.util.ScanStatusBottomSheet
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import com.secure.jnet.wallet.util.fontFamily
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetCardStateScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (String) -> Unit,
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
        contentColor = Color.Black,
        containerColor = Color.Black,
        modifier = Modifier.background(Color.Black),
        topBar = {
            TopAppBar(
                title = {
                    Text("Get Enrollment Status", fontFamily = fontFamily)
                },
                actions = {
                    IconButton(onClick = { onNavigate(NAV_SETTINGS) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .padding(top = 150.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))
            LottieAnimation(composition)

            Text(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                color = Color.White,
                text = "Place your card on a flat, non-metallic surface then place a phone on top leaving sensor accessible for finger print scanning.",
                textAlign = TextAlign.Center,
                fontFamily = fontFamily,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
            Button(
                modifier = Modifier
                    .padding(start = 17.dp, end = 17.dp, bottom = 30.dp)
                    .fillMaxWidth(),
                onClick = {
                    nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
                }
            ) {
                Text("Scan Card")
            }
        }

        ScanStatusBottomSheet(
            sheetState = sheetState,
            showStatus = showStatus,
            onButtonClicked = {

                if (showStatus is ShowStatus.Result && showStatus.result is NfcActionResult.BiometricEnrollment) {
                    if (showStatus.result.isStatusEnrollment) {
                        println("navigate to enroll")
                        onNavigate(NAV_ENROLL)
                    } else {
                        println("navigate to lock")
                        onNavigate(NAV_LOCK)
                    }

                } else {
                    println("unexpected: showStatus $showStatus")
                }
                nfcViewModel.resetNfcAction()
            },
            onDismiss = {
                nfcViewModel.resetNfcAction()
            }
        )

    }
}
