package com.sentryenterprises.sentry.enrollment

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import com.sentryenterprises.sentry.sdk.SentrySdk
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import kotlin.concurrent.thread

sealed class ShowStatus {
    data object Hidden : ShowStatus()
    data object Scanning : ShowStatus()
    data object CardFound : ShowStatus()
    data class Error(val message: String) : ShowStatus()
    data class Result(val result: NfcActionResult) : ShowStatus()
}

class NfcViewModel : ViewModel() {

    private val _nfcProgress = MutableStateFlow<Int?>(null)
    val nfcProgress = _nfcProgress.asStateFlow()

    private val _fingerProgress = MutableStateFlow<BiometricProgress?>(null)
    val fingerProgress = _fingerProgress.asStateFlow()

    private val resetEnrollFingerPrintNeeded = MutableStateFlow<Boolean>(false)

    private val _nfcActionResult = MutableStateFlow<Result<NfcActionResult>?>(null)
    val nfcActionResult = _nfcActionResult.asStateFlow()

    private val _nfcAction = MutableStateFlow<NfcAction?>(null)
    val nfcAction = _nfcAction.asStateFlow()

    val showStatus = nfcAction.combine(nfcActionResult) { action, result ->
        action to result
    }.combine(nfcProgress) { (action, result), progress ->
        if (action == null && result == null && progress == null) {
            ShowStatus.Hidden
        } else if (action != null) {
            if (progress == null) {
                ShowStatus.Scanning
            } else {
                ShowStatus.CardFound
            }
//        } else if (result != null && result is NfcActionResult.ErrorResult) {
//            ShowStatus.Error(internalException?.message ?: result.error)
        } else if (result != null) {
            if (result.isSuccess) {
                ShowStatus.Result(result.getOrThrow())
            } else {
                ShowStatus.Error(result.exceptionOrNull()?.message ?: "Unknown error $result")
            }
        } else {
            ShowStatus.Error("Unknown error, likely due to incorrect placement.")
        }
    }

    private var tag: Tag? = null

    private val sentrySdk by lazy {
        SentrySdk(
            enrollCode = byteArrayOf(1, 1, 1, 1, 1, 1),
        )
    }


    @Volatile
    private var inProcess = false

    fun onTagDiscovered(tag: Tag?) {
        Timber.d("----> onTagDiscovered() $tag")

        this@NfcViewModel.tag = tag

        nfcAction.value?.let {
            Timber.d("----> Starting Action $nfcAction")
            startCardExchange(it)
        } ?: Timber.d("----> Tag discovered but action was null")
    }

    fun resetNfcAction() {
        _nfcProgress.value = null
        _nfcActionResult.value = null
        _nfcAction.value = null
        _fingerProgress.value = null
    }

    fun startNfcAction(nfcAction: NfcAction?) {
        Timber.d("----> startNfcAction() $nfcAction")
        _nfcProgress.value = null
        _nfcActionResult.value = null
        _nfcAction.value = nfcAction

        nfcAction?.let { startCardExchange(it) }
    }

    @Synchronized
    private fun startCardExchange(nfcAction: NfcAction) {
        val tag = this.tag
        if (tag == null) {
            Timber.d("----> startCardExchange() tag was null, exiting")
            return
        }
        if (inProcess) {
            Timber.d("----> already in process, exiting")
            return
        }

        if (!sentrySdk.openConnection(tag)) {
            Timber.d("----> could not open connection, exiting")
            return
        }

        inProcess = true

        _nfcProgress.value = 1

        thread {
            try {
                when (nfcAction) {
                    is NfcAction.GetEnrollmentStatus -> {
                        sentrySdk.getEnrollmentStatus(tag)
                    }

                    is NfcAction.EnrollFingerprint -> {
                        try {
                            sentrySdk.enrollFinger(
                                tag = tag,
                                resetOnFirstCall = resetEnrollFingerPrintNeeded.value,
                                onBiometricProgressChanged = { progress ->
                                    _fingerProgress.value = progress
                                }
                            ).also {
                                resetEnrollFingerPrintNeeded.value = false
                            }.let {
                                Result.success(it)
                            }
                        } catch (e: Exception) {
//                        } catch (e: SentrySDKError.EnrollVerificationError) { TODO should be hitting this
                            resetEnrollFingerPrintNeeded.value = true
                            _fingerProgress.value = null
                            Timber.e(e)
                            Result.failure(e)
//                            NfcActionResult.EnrollFingerprint.Failed
                        }
                    }

                    is NfcAction.VerifyBiometric -> {
                        sentrySdk.validateFingerprint(tag)
                    }

                    is NfcAction.ResetBiometricData -> {
                        sentrySdk.resetCard(tag)
                    }

                    is NfcAction.GetVersionInformation -> {
                        sentrySdk.getCardSoftwareVersions(tag)
                    }
                }.let { nfcActionResult: Result<NfcActionResult> ->
                    Timber.d("-----> $nfcAction = $nfcActionResult")

                    _nfcActionResult.value = nfcActionResult
                }

            } catch (e: Exception) {
                Timber.e(e)

                _nfcActionResult.value = Result.failure(e)
            } finally {
                sentrySdk.closeConnection(tag)

                _nfcAction.value = null
                _nfcProgress.value = null

                inProcess = false
            }
        }
    }


}