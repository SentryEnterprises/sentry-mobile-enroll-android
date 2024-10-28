package com.sentryenterprises.sentry.enrollment.util

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentryenterprises.sentry.enrollment.ShowStatus
import com.sentryenterprises.sentry.sdk.models.NfcActionResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanStatusBottomSheet(
    sheetState: SheetState,
    showStatus: ShowStatus,
    cardFoundText: String = "Please do not move the phone or card.",
    onShowResultText: (NfcActionResult) -> Pair<String, String?>,
    onButtonClicked: (() -> Unit)?,
    onDismiss: (() -> Unit)?,
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss ?: {},
            sheetState = sheetState,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

                if (showStatus is ShowStatus.Scanning) {
                    CircularProgressIndicator()
                }
                val (statusTitle, statusText) = when (showStatus) {
                    ShowStatus.CardFound -> "Card Found" to cardFoundText
                    is ShowStatus.Error -> "Scan Error" to showStatus.message
                    is ShowStatus.Result -> onShowResultText(showStatus.result)
                    ShowStatus.Scanning -> "Ready to Scan" to "Place your card under the phone to establish connection."
                    ShowStatus.Hidden -> "" to "" // Nothing
                }

                Text(
                    modifier = Modifier.padding(bottom = 5.dp, top = 17.dp),
                    text = statusTitle,
                    fontSize = 23.sp,
                    fontWeight = Bold,
                )
                if (statusText != null) {
                    Text(
                        modifier = Modifier.padding(bottom = 25.dp, top = 17.dp),
                        textAlign = TextAlign.Center,
                        text = statusText,
                        fontWeight = Normal,
                    )
                }

                onButtonClicked?.let {
                    val okButtonText =
                        if (showStatus is ShowStatus.Result && showStatus.result is NfcActionResult.BiometricEnrollment) {
                            if (showStatus.result.isStatusEnrollment) {
                                "Enroll"
                            } else {
                                "Verify"
                            }
                        } else {
                            when (showStatus) {
                                ShowStatus.Hidden -> "" // Nothing

                                is ShowStatus.Result,
                                is ShowStatus.Error -> "Ok"

                                ShowStatus.CardFound,
                                ShowStatus.Scanning -> "Cancel"
                            }
                        }
                    SentryButton(
                        modifier = Modifier.padding(bottom = 30.dp),
                        text = okButtonText,
                        onClick = onButtonClicked
                    )
                }
            }

        }
    }
}