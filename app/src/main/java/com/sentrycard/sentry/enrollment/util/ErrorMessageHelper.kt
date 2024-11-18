package com.sentrycard.sentry.enrollment.util

import android.nfc.TagLostException
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sentrycard.sentry.enrollment.R
import com.sentrycard.sentry.sdk.apdu.localizedErrorMessage
import com.sentrycard.sentry.sdk.presentation.SentrySDKError
import com.sentrycard.sentry.sdk.presentation.SentrySDKError.EnrollCodeDigitOutOfBounds

import com.sentrycard.sentry.sdk.apdu.localizedErrorMessage as defaultErrorDecoding

@Composable
fun Throwable?.getDecodedMessage() = when (this) {
    is SentrySDKError -> this.localizedErrorMessage()
    is TagLostException -> stringResource(R.string.communication_with_the_card_has_failed_please_move_the_phone_away_from_the_card_briefly_to_reset_the_card_then_try_again)
    else -> this?.localizedMessage ?: stringResource(R.string.unknown_error)
}

@Composable
fun SentrySDKError.localizedErrorMessage() = when (this) {
    is EnrollCodeDigitOutOfBounds -> stringResource(R.string.individual_enroll_code_digits_must_be_in_the_range_0_9)
    else -> defaultErrorDecoding()
}