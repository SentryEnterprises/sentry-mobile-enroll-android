package com.sentryenterprises.sentry.enrollment

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentryenterprises.sentry.sdk.SentrySdk
import com.sentryenterprises.sentry.sdk.apdu.getDecodedMessage
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
    val resetReaderEvents = Channel<Boolean>()


    val showStatus = combine(nfcAction, nfcActionResult, nfcProgress) { action, result, progress ->
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
                ShowStatus.Error(
                    result.exceptionOrNull()?.getDecodedMessage() ?: "Unknown error $result"
                )
            }
        } else {
            ShowStatus.Error("Unknown error, likely due to incorrect placement.")
        }
    }

    private var tag: Tag? = null

    private val sentrySdk = SentrySdk(
        enrollCode = byteArrayOf(1, 1, 1, 1, 1, 1),
    )

    val sdkVersion = sentrySdk.sdkVersion

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
        Timber.d("----> resetNfcAction()")
        _nfcProgress.value = null
        _nfcActionResult.value = null
        _nfcAction.value = null
        _fingerProgress.value = null

        viewModelScope.launch {
            resetReaderEvents.send(true)
        }
//        tag?.let {
////            https://stackoverflow.com/a/69615803/247325
//            val halt = byteArrayOf(0x35, 0x30, 0x30,0x30, 0x00)
//            val isoDep = IsoDep.get(it)
//            if (isoDep.isConnected) {
//                Timber.d("----> resetNfcAction() halt sent")
//                isoDep.transceive(halt)
//            } else {
//                Timber.d("----> resetNfcAction()tag not connected")
//            }
//
//        } ?: run { Timber.d("----> resetNfcAction()tag null") }
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

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (nfcAction) {
                    is NfcAction.GetEnrollmentStatus -> {
                        try {
                            Result.success(sentrySdk.getEnrollmentStatus(tag))
                        } catch (e: Exception) {
                            Result.failure(e)
                        }
                    }

                    is NfcAction.EnrollFingerprint -> {
                        try {
                            sentrySdk.enrollFingerprint(
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
                            resetEnrollFingerPrintNeeded.value = true
                            _fingerProgress.value = null
                            Timber.e(e)
                            Result.failure(e)
                        }
                    }

                    is NfcAction.VerifyBiometric -> {
                        try {
                            Result.success(sentrySdk.validateFingerprint(tag))
                        } catch (e: Exception) {
                            Result.failure(e)
                        }
                    }

                    is NfcAction.ResetBiometricData -> {
                        try {
                            Result.success(sentrySdk.resetCard(tag))
                        } catch (e: Exception) {
                            Result.failure(e)
                        }

                    }

                    is NfcAction.GetVersionInformation -> {
                        try {
                            Result.success(sentrySdk.getCardSoftwareVersions(tag))
                        } catch (e: Exception) {
                            Result.failure(e)
                        }
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