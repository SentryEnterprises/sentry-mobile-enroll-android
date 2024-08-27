package com.sentryenterprises.sentry.enrollment.versioninfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import com.secure.jnet.wallet.BuildConfig

import com.secure.jnet.wallet.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentryenterprises.sentry.enrollment.NAV_SETTINGS
import com.sentryenterprises.sentry.enrollment.NfcViewModel
import com.sentryenterprises.sentry.enrollment.ShowStatus
import com.sentryenterprises.sentry.enrollment.util.ScanStatusBottomSheet
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionInfoScreen(
    modifier: Modifier = Modifier,
    nfcViewModel: NfcViewModel,
    onNavigate: (String) -> Unit,
) {
    val nfcAction = nfcViewModel.nfcAction.collectAsState().value
    val nfcActionResult = nfcViewModel.nfcActionResult.collectAsState().value
    val progress = nfcViewModel.nfcProgress.collectAsState().value
    val sheetState = rememberModalBottomSheetState()
    val showStatus = nfcViewModel.showStatus.collectAsState(ShowStatus.Hidden).value

    LaunchedEffect(nfcAction) {
        if (nfcAction is NfcAction.GetVersionInformation && nfcActionResult != null) {
            sheetState.expand()
        } else {
            sheetState.hide()
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
                    Text("Version Information")
                },
            )
        }
    ) { paddingInsets ->
        Column(
            modifier = Modifier
                .padding(paddingInsets)
                .fillMaxSize(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.attach_card))
                LottieAnimation(composition)

                Text(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                    color = Color.White,
                    text = "Place your card on a flat, non-metallic surface then place the phone on top.",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            }
            if (nfcActionResult is NfcActionResult.VersionInformation){

                val info =
                    mapOf(
                        "App Version" to BuildConfig.VERSION_CODE,
                        "OS Version" to nfcActionResult.osVersion,
                        "Enroll Version" to nfcActionResult.enrollAppletVersion,
                        "CVM Version" to nfcActionResult.cvmAppletVersion,
                        "Verify Version" to nfcActionResult.verifyAppletVersion
                    )

                LazyColumn(Modifier.weight(1f)) {
                    info.forEach {
                        item {
                            Column() {
                                Text(
                                    modifier = Modifier.padding(
                                        start = 14.dp,
                                        top = 5.dp,
                                        bottom = 5.dp
                                    ),
                                    text = it.key,
                                    color = Color.Gray
                                )
                                Text(
                                    modifier = Modifier.padding(start = 17.dp, bottom = 5.dp),
                                    text = it.value.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }

            }

            Button(
                modifier = Modifier
                    .padding(start = 17.dp, end = 17.dp, bottom = 30.dp, top = 15.dp)
                    .fillMaxWidth(),
                onClick = {
                    nfcViewModel.startNfcAction(NfcAction.GetVersionInformation)
                }
            ) {
                Text("Scan for card")
            }
        }


    }


    ScanStatusBottomSheet(
        sheetState = sheetState,
        showStatus = showStatus,
        onButtonClicked = {
        },
        onDismiss = {
        }
    )
    ScanningStatusBottomSheet(
        sheetState = sheetState,
        nfcActionResult = nfcActionResult,
        nfcAction = nfcAction,
        progress = progress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanningStatusBottomSheet(
    sheetState: SheetState,
    nfcActionResult: NfcActionResult?,
    progress: Int?,
    nfcAction: NfcAction?
) {

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
            if (progress != null || nfcAction != null) {
                CircularProgressIndicator(color = Color.White)
            } else {

            }
            if (nfcActionResult != null && nfcActionResult is NfcActionResult.ResetBiometrics) {

                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 25.dp),
                    text = "Reset Result",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 50.dp),
                    text = "The reset was successful",
                    color = Color.White,
                )

            } else {

                val (statusText, isProgressing) = if (progress == null && nfcAction is NfcAction.GetVersionInformation) {
                    "Scanning" to true
                } else if (progress != null) {
                    "Card found" to true
                } else {
                    "An error occurred. Please try again." to false
                }


                Text(
                    modifier = Modifier.padding(start = 17.dp, bottom = 25.dp),
                    text = statusText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}