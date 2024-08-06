package com.secure.jnet.wallet.presentation.cardState

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.presentation.NAV_SETTINGS
import com.secure.jnet.wallet.presentation.NfcViewModel
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
    val sheetState = rememberModalBottomSheetState()
    val actionState = nfcViewModel.nfcActionResult.collectAsState().value
    val enrollmentStatus = nfcViewModel.showEnrollmentStatus.collectAsState(null).value

    LaunchedEffect(actionState) {
        if (
            actionState != null
            && (actionState is NfcActionResult.ErrorResult
                    || actionState is NfcActionResult.EnrollmentStatusResult)
        ) {
            sheetState.expand()
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

            if (nfcProgress != null) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = {},
                sheetState = sheetState,
            ) {
                if (actionState != null && actionState is NfcActionResult.ErrorResult) {

                    Text(
                        modifier = Modifier.padding(17.dp),
                        text = actionState.error,
                        color = Color.White,
                        fontFamily = fontFamily
                    )

                } else if (enrollmentStatus != null) {

                    Text(
                        modifier = Modifier.padding(start = 17.dp, bottom = 25.dp),
                        text = enrollmentStatus.first,
                        color = Color.White,
                        fontFamily = fontFamily,
                        fontWeight = Bold,
                    )

                    Text(
                        modifier = Modifier.padding(start = 17.dp, bottom = 50.dp),
                        text = enrollmentStatus.second,
                        color = Color.White,
                        fontFamily = fontFamily
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 17.dp, bottom = 50.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        val scope = rememberCoroutineScope()

                        Button(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                }
                            }
                        ) {
                            Text("Ok")
                        }

                    }

                }
            }
        }
    }
}