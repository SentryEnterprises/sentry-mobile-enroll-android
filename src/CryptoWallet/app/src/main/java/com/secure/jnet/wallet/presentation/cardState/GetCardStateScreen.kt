package com.secure.jnet.wallet.presentation.cardState

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import com.secure.jnet.wallet.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.jnet.jcwkit.models.BiometricMode
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.presentation.NAV_SETTINGS
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.presentation.ShowStatus
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import com.secure.jnet.wallet.util.fontFamily
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetCardStateScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (String) -> Unit,
) {

    val nfcProgress = nfcViewModel.nfcProgress.collectAsState().value
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val actionResult = nfcViewModel.nfcActionResult.collectAsState().value
    val enrollmentStatus = nfcViewModel.showEnrollmentStatus.collectAsState(null).value
    val action = nfcViewModel.nfcAction.collectAsState().value
    val progress = nfcViewModel.nfcProgress.collectAsState().value
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

        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    nfcViewModel.startNfcAction(null)
                },
                sheetState = sheetState,
            ) {
                if (nfcProgress != null) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(start = 17.dp),
                        color = Color.White
                    )
                }
                val (statusTitle, statusText) = when (showStatus) {
                    ShowStatus.CardFound -> "Card Found" to "Please do not move the phone or card."
                    is ShowStatus.Error -> "Scan Error" to showStatus.message
                    is ShowStatus.Result -> {
                        if (showStatus.result is NfcActionResult.EnrollmentStatusResult) {
                            val (title, message) = if (showStatus.result.biometricMode == BiometricMode.VERIFY_MODE) {
                                "Enrollment Status: Enrolled" to "This card is enrolled. A fingerprint is recorded on this card. Click OK to continue."
                            } else {
                                "Enrollment Status: Not enrolled" to "This card is not enrolled. No fingerprints are recorded on this card. Click OK to continue."
                            }

                            title to message
                        } else error("Unexpected state $showStatus")
                    }

                    ShowStatus.Scanning -> "Ready to Scan" to "Place your card under the phone to establish connection."
                    ShowStatus.Hidden -> "" to "" // Nothing
                }

                val okButtonText = when (showStatus) {
                    ShowStatus.Hidden -> ""

                    is ShowStatus.Result,
                    is ShowStatus.Error -> "Ok"

                    ShowStatus.CardFound,
                    ShowStatus.Scanning -> "Cancel"
                }


                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 5.dp, top = 17.dp),
                    text = statusTitle,
                    fontSize = 23.sp,
                    color = Color.LightGray,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 25.dp, top = 17.dp),
                    text = statusText,
                    color = Color.White,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal,
                )
                Button(
                    modifier = Modifier
                        .padding(start = 17.dp, bottom = 25.dp, end = 17.dp)
                        .fillMaxWidth(),
                    onClick = {
                        nfcViewModel.startNfcAction(null)
                    }
                ) {
                    Text(okButtonText)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}