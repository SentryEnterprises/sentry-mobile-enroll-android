package com.sentrycard.sentry.enrollment.util

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentrycard.sentry.enrollment.R
import com.sentrycard.sentry.enrollment.ShowStatus
import com.sentrycard.sentry.sdk.models.NfcActionResult

// TODO: Localize all these strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanStatusBottomSheet(
    sheetState: SheetState,
    showStatus: ShowStatus,
    cardFoundText: String = stringResource(R.string.please_do_not_move_the_phone_or_card),
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
                    ShowStatus.CardFound -> stringResource(R.string.card_found)to cardFoundText
                    is ShowStatus.Error -> stringResource(R.string.communication_failure) to showStatus.error?.getDecodedMessage()
                    is ShowStatus.Result -> onShowResultText(showStatus.result)
                    ShowStatus.Scanning -> stringResource(R.string.ready_to_scan) to stringResource(R.string.place_your_card_under_the_phone_to_establish_connection)
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
                                stringResource(R.string.enroll)
                            } else {
                                stringResource(R.string.verify)
                            }
                        } else {
                            when (showStatus) {
                                ShowStatus.Hidden -> "" // Nothing

                                is ShowStatus.Result,
                                is ShowStatus.Error -> stringResource(R.string.ok)

                                ShowStatus.CardFound,
                                ShowStatus.Scanning -> stringResource(R.string.cancel)
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