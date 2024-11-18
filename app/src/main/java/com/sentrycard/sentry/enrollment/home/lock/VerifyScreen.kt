package com.sentrycard.sentry.enrollment.home.lock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentrycard.sentry.enrollment.NfcViewModel
import com.sentrycard.sentry.enrollment.R
import com.sentrycard.sentry.enrollment.Screen
import com.sentrycard.sentry.enrollment.ShowStatus
import com.sentrycard.sentry.enrollment.util.ScanStatusBottomSheet
import com.sentrycard.sentry.enrollment.util.SentryButton
import com.sentrycard.sentry.sdk.models.FingerprintValidation
import com.sentrycard.sentry.sdk.models.NfcAction
import com.sentrycard.sentry.sdk.models.NfcActionResult


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
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.verify_fingerprint))
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

            Text(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                text = stringResource(R.string.place_your_card_on_a_flat_non_metallic_surface_then_place_a_phone_on_top_leaving_sensor_accessible_for_finger_print_scanning),
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
                text = stringResource(R.string.verify_fingerprint),
                onClick = {
                    nfcViewModel.startNfcAction(NfcAction.VerifyBiometric)
                }
            )
        }

        val context = LocalContext.current
        ScanStatusBottomSheet(
            sheetState = sheetState,
            showStatus = showStatus,
            cardFoundText = stringResource(R.string.place_your_finger_on_the_card),
            onShowResultText = { result ->
                if (result is NfcActionResult.VerifyBiometric) {
                    context.getString(R.string.verification_status) to when (result.fingerprintValidation) {
                        FingerprintValidation.MatchValid -> {
                            context.getString(R.string.fingerprint_successfully_verified)
                        }

                        FingerprintValidation.MatchFailed -> {
                            context.getString(R.string.fingerprint_did_not_match)
                        }

                        else -> {
                            context.getString(R.string.this_card_is_not_enrolled)
                        }
                    }
                } else error(context.getString(R.string.unexpected_state, showStatus))
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

