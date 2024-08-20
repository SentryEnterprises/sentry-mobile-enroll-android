package com.secure.jnet.wallet.presentation

import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.jcwkit.JCWIOException
import com.secure.jnet.jcwkit.JCWKit
import com.secure.jnet.jcwkit.NonNativeSmartCardApduCallback
import com.secure.jnet.jcwkit.SmartCardApduCallback
import com.secure.jnet.jcwkit.models.BiometricMode
import com.secure.jnet.jcwkit.utils.formatted
import com.secure.jnet.wallet.data.JCWCardWallet
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.util.SingleLiveEvent
import com.sentryenterprises.sentry.sdk.SentrySdk
import com.sentryenterprises.sentry.sdk.models.BiometricMode.Enrollment
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    private val _nfcBiometricProgress = SingleLiveEvent<Int>()
    val nfcBiometricProgress: LiveData<Int> = _nfcBiometricProgress

    private val _nfcProgress = MutableStateFlow<Int?>(null)
    val nfcProgress = _nfcProgress.asStateFlow()

    private val _fingerProgress = MutableStateFlow<BiometricProgress?>(null)
    val fingerProgress = _fingerProgress.asStateFlow()

    private val _nfcActionResult = MutableStateFlow<NfcActionResult?>(null)
    val nfcActionResult = _nfcActionResult.asStateFlow()

    private val _versionInfo = MutableStateFlow<String>("")
    val versionInformation = _versionInfo.asStateFlow()

    private val _nfcAction = MutableStateFlow<NfcAction?>(null)
    val nfcAction = _nfcAction.asStateFlow()

    val showStatus = nfcAction.combine(nfcActionResult) { action, result ->
        action to result
    }.combine(nfcProgress) { (action, result), progress ->
        val internalException = this.internalException
        if (action == null && result == null && progress == null) {
            ShowStatus.Hidden
        } else if (action != null && progress == null) {
            ShowStatus.Scanning
        } else if (action != null && progress != null) {
            ShowStatus.CardFound
        } else if (result != null && result is NfcActionResult.ErrorResult) {
            ShowStatus.Error(internalException?.message ?: result.error)
        } else if (result != null) {
            ShowStatus.Result(result)
        } else if (internalException != null) {
            ShowStatus.Error(internalException.message ?: "Unknown error")
        } else {
            ShowStatus.Error("Unknown error, likely due to incorrect placement.")
        }
    }


    val showEnrollmentStatus = _nfcActionResult.map {
        if (it != null
            && it is NfcActionResult.EnrollmentStatusResult
        ) {
            val (title, message) = if (it.biometricMode == BiometricMode.VERIFY_MODE) {
                "Enrollment Status: Enrolled" to "This card is enrolled. A fingerprint is recorded on this card. Click OK to continue."
            } else {
                "Enrollment Status: Not enrolled" to "This card is not enrolled. No fingerprints are recorded on this card. Click OK to continue."
            }

            title to message
        } else {
            null
        }
    }

    private var tag: Tag? = null
    private var mIsoDep: IsoDep? = null

    private var internalException: Exception? = null

    private val callBack = SmartCardApduCallback { dataIn, dataInLen, dataOut, dataOutLen ->
        internalException = null

        try {
            val dataInBytes = dataIn?.getByteArray(0, dataInLen)!!

            dataInBytes.logCommand()

            val response = mIsoDep!!.transceive(dataInBytes)
            val responseLength = response.size

            response.logResponse()

            dataOut!!.write(0, response, 0, responseLength)
            dataOutLen!!.setInt(0, responseLength)

            0
        } catch (e: Exception) {
            Timber.e(e, "callback error")
            internalException = e
            throw e

            1000
        }
    }
    private val nonNativeCallBack = NonNativeSmartCardApduCallback { dataIn ->
        try {
            dataIn.logCommand()

            val response = mIsoDep!!.transceive(dataIn)

            response.logResponse()

            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private val jcwCardWallet by lazy { JCWCardWallet(callBack, nonNativeCallBack, JCWKit()) }
    private val sentrySdk by lazy {
        SentrySdk(
            enrollCode = byteArrayOf(1, 1, 1, 1, 1, 1),
//            enrollCode = byteArrayOf(1, 2, 3, 4),
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
                        sentrySdk.getEnrollmentStatus { dataIn: ByteArray ->
                            Result.success(mIsoDep!!.transceive(dataIn))
                        }.let {
                            NfcActionResult.BiometricEnrollmentResult(
                                it.mode == Enrollment
                            )
                        }
                    }

                    is NfcAction.EnrollFingerprint -> {
                        sentrySdk.enrollFinger(
                            iso7816Tag = { data -> Result.success(mIsoDep!!.transceive(data)) },
                            onBiometricProgressChanged = { progress ->
                                _fingerProgress.value = progress
                            }
                        ).let {
                            NfcActionResult.BiometricEnrollmentResult(
                                it.mode == Enrollment
                            )
                        }

                    }

                    is NfcAction.VerifyBiometric -> {
                        jcwCardWallet.verifyBiometric()
                    }

                    is NfcAction.ResetBiometricData -> {
                        jcwCardWallet.resetBiometricData()
                    }

                    is NfcAction.GetVersionInformation -> {
                        jcwCardWallet.versionInformation().also {
                            _versionInfo.value = it.version
                        }
                    }
                }.let { nfcActionResult ->
                    Timber.d("-----> $nfcAction = $nfcActionResult")
                    _nfcActionResult.value = nfcActionResult
                }

            } catch (e: JCWIOException) {
                Timber.e(e)
                var errorMessage = ErrorMessageHelper(e).getErrorMessage()

                if (e.errorCode == 1000) {
                    errorMessage = ErrorMessageHelper(internalException).getErrorMessage()
                }

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

    private fun ByteArray.logCommand() {
        Timber.d("---------------------------")
        Timber.d(
            "=> ${
                this.formatted()
            }"
        )
    }

    private fun ByteArray.logResponse() {
        Timber.d(
            "<= ${
                this.formatted()
            }"
        )
        Timber.d("---------------------------\n")
    }
}