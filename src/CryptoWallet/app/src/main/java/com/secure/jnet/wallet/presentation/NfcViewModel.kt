package com.secure.jnet.wallet.presentation

import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.lifecycle.ViewModel
import com.sentryenterprises.sentry.sdk.SentrySdk
import com.sentryenterprises.sentry.sdk.models.BiometricMode.Enrollment
import com.sentryenterprises.sentry.sdk.models.BiometricMode.Verification
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcAction
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import kotlin.Result
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

    private val _nfcActionResult = MutableStateFlow<NfcActionResult?>(null)
    val nfcActionResult = _nfcActionResult.asStateFlow()

    private val _nfcAction = MutableStateFlow<NfcAction?>(null)
    val nfcAction = _nfcAction.asStateFlow()

    val showStatus = nfcAction.combine(nfcActionResult) { action, result ->
        action to result
    }.combine(nfcProgress) { (action, result), progress ->
//        val internalException = this.internalException
        if (action == null && result == null && progress == null) {
            ShowStatus.Hidden
        } else if (action != null && progress == null) {
            ShowStatus.Scanning
        } else if (action != null && progress != null) {
            ShowStatus.CardFound
//        } else if (result != null && result is NfcActionResult.ErrorResult) {
//            ShowStatus.Error(internalException?.message ?: result.error)
//        } else if (result != null) {
//            ShowStatus.Result(result)
//        } else if (internalException != null) {
//            ShowStatus.Error(internalException.message ?: "Unknown error")
        } else {
            ShowStatus.Error("Unknown error, likely due to incorrect placement.")
        }
    }

    private var tag: Tag? = null
    private var mIsoDep: IsoDep? = null

    private val sentrySdk by lazy {
        SentrySdk(
            enrollCode = byteArrayOf(1, 1, 1, 1, 1, 1),
        )
    }

    private val tagCallback = object : NfcIso7816Tag {
        override fun transceive(dataIn: ByteArray): Result<ByteArray> {
            return Result.success(mIsoDep!!.transceive(dataIn))
        }

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
    }

    fun startNfcAction(nfcAction: NfcAction?) {
        Timber.d("----> startNfcAction() $nfcAction")
        _nfcProgress.value = null
        _nfcActionResult.value = null
        _nfcAction.value = nfcAction

//        startCardExchange(nfcAction)
    }

    @Synchronized
    private fun startCardExchange(nfcAction: NfcAction) {
        if (tag == null) {
            Timber.d("----> startCardExchange() tag was null, exiting")
            return
        }
        if (inProcess) {
            Timber.d("----> already in process, exiting")
            return
        }

        if (!openConnection()) {
            Timber.d("----> could not open connection, exiting")
            return
        }

        inProcess = true

        _nfcProgress.value = 1

        thread {
            try {
                when (nfcAction) {
                    is NfcAction.GetEnrollmentStatus -> {
                        sentrySdk.getEnrollmentStatus(tagCallback)
                    }

                    is NfcAction.EnrollFingerprint -> {
                        sentrySdk.enrollFinger(
                            iso7816Tag = tagCallback,
                            onBiometricProgressChanged = { progress ->
                                _fingerProgress.value = progress
                            }
                        )
                    }

                    is NfcAction.VerifyBiometric -> {
                        sentrySdk.validateFingerprint(tagCallback)
                    }

                    is NfcAction.ResetBiometricData -> {
                        sentrySdk.resetCard(tagCallback)
                    }

                    is NfcAction.GetVersionInformation -> {
                        sentrySdk.getCardSoftwareVersions(tagCallback)
                    }
                }.let { nfcActionResult ->
                    Timber.d("-----> $nfcAction = $nfcActionResult")
                    _nfcActionResult.value = nfcActionResult
                }

            } catch (e: Exception) {
                Timber.e(e)
                var errorMessage = ErrorMessageHelper(e).getErrorMessage()

                val nfcActionResult = NfcActionResult.ErrorResult(
                    errorMessage
                )
                _nfcActionResult.value = nfcActionResult
            } catch (e: Exception) {
                Timber.e(e)

                val nfcActionResult = NfcActionResult.ErrorResult(
                    "${e.message}"
                )
                _nfcActionResult.value = nfcActionResult
            } finally {
                closeConnection()

                _nfcAction.value = null
                _nfcProgress.value = null

                inProcess = false
            }
        }
    }

    private fun openConnection(): Boolean {
        // TODO: We may need to expose exceptions here
        return try {
            mIsoDep = IsoDep.get(tag)
            mIsoDep!!.timeout = 30000
            mIsoDep!!.connect()
            true
        } catch (e: SecurityException) {
            Timber.e("Ignore SecurityException Tag out of date")
            Timber.e(e)
            false
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    private fun closeConnection() {
        try {
            mIsoDep?.close()
        } catch (e: SecurityException) {
            Timber.e("Ignoring security exception for tag out of date")
            Timber.e(e)
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            mIsoDep = null
        }
    }

}