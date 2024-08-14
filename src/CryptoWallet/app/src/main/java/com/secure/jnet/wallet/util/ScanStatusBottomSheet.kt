package com.secure.jnet.wallet.util

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.jnet.jcwkit.models.BiometricMode
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.presentation.ShowStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanStatusBottomSheet(
    sheetState: SheetState,
    showStatus: ShowStatus,
    onButtonClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            if (showStatus is ShowStatus.Scanning) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(start = 17.dp),
                    color = Color.White
                )
            }
            val (statusTitle, statusText) = when (showStatus) {
                ShowStatus.CardFound -> "Card Found" to "Please do not move the phone or card."
                is ShowStatus.Error -> "Scan Error" to showStatus.message
                is ShowStatus.Result -> {
                    if (showStatus.result is NfcActionResult.BiometricEnrollmentResult) {
                        if (showStatus.result.isStatusEnrollment) {
                            "Not Enrolled" to "This card is not enrolled. No fingerprints are recorded on this card. Click OK to continue."
                        } else {
                            "Enrolled" to "This card is enrolled. A fingerprint is recorded on this card. Click OK to continue."
                        }
                    } else if (showStatus.result is NfcActionResult.VerifyBiometricResult) {
                        "Verification Status" to if (showStatus.result.isBiometricCorrect) {
                            "Fingerprint successfully verified!"
                        } else {
                            "Fingerprint did not match"
                        }
                    } else if (showStatus.result is NfcActionResult.EnrollmentStatusResult) {
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
                ShowStatus.Hidden -> "" // Nothing

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
                fontWeight = Bold,
            )
            Text(
                modifier = Modifier.padding(start = 17.dp, bottom = 25.dp, top = 17.dp),
                text = statusText,
                color = Color.White,
                fontFamily = fontFamily,
                fontWeight = Normal,
            )
            Button(
                modifier = Modifier
                    .padding(start = 17.dp, bottom = 50.dp, end = 17.dp)
                    .fillMaxWidth(),
                onClick = onButtonClicked
            ) {
                Text(okButtonText)
            }
        }
    }
}