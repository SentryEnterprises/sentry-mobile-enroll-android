package com.secure.jnet.wallet.presentation

import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.jcwkit.JCWIOException
import com.secure.jnet.jcwkit.JCWKit
import com.secure.jnet.jcwkit.NonNativeSmartCardApduCallback
import com.secure.jnet.jcwkit.SmartCardApduCallback
import com.secure.jnet.jcwkit.utils.formatted
import com.secure.jnet.wallet.data.JCWCardWallet
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.util.SingleLiveEvent
import com.sentryenterprises.sentry.sdk.SentrySdk
import timber.log.Timber
import kotlin.Result
import kotlin.concurrent.thread

class NfcViewModel : ViewModel() {

    private val _nfcShowProgress = SingleLiveEvent<Boolean>()
    val nfcShowProgress: LiveData<Boolean> = _nfcShowProgress

    private val _nfcBiometricProgress = SingleLiveEvent<Int>()
    val nfcBiometricProgress: LiveData<Int> = _nfcBiometricProgress

    private val _nfcProgress = SingleLiveEvent<Int>()
    val nfcProgress: LiveData<Int> = _nfcProgress

    private val _nfcActionResult = SingleLiveEvent<NfcActionResult>()
    val nfcActionResult: SingleLiveEvent<NfcActionResult> = _nfcActionResult

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
            Timber.e(e)
            internalException = e

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
            enrollCode = byteArrayOf(1, 2, 3, 4),
        )
    }

    private var nfcAction: NfcAction? = null

    @Volatile
    private var inProcess = false

    fun onTagDiscovered(tag: Tag?) {
        Timber.d("----> onTagDiscovered() $tag")

        this@NfcViewModel.tag = tag

        nfcAction?.let {
            Timber.d("----> Starting Action $nfcAction")
            startCardExchange(it)
        } ?: Timber.d("----> Tag discovered but action was null")
    }

    fun startNfcAction(nfcAction: NfcAction) {
        Timber.d("----> startNfcAction() $nfcAction")

        this@NfcViewModel.nfcAction = nfcAction

        startCardExchange(nfcAction)
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

        _nfcShowProgress.postValue(true)

        thread {
            try {
                when (nfcAction) {
                    is NfcAction.GetEnrollmentStatus -> {
//                        sentrySdk.getEnrollmentStatus()
                        jcwCardWallet.getEnrollmentStatus(nfcAction.pinCode).also {
                            Timber.d("-----> getEnrollmentStatus = $it")
                        }
                    }

                    is NfcAction.BiometricEnrollment -> {
                        jcwCardWallet.enrollFinger { progress ->
                            _nfcBiometricProgress.postValue(progress)
                        }.also {
                            Timber.d("-----> enrollFinger = $it")
                        }
                    }

                    is NfcAction.VerifyBiometric -> {
                        jcwCardWallet.verifyBiometric().also {
                            Timber.d("-----> verifyBiometric = $it")
                        }
                    }
                }.let { nfcActionResult ->
                    _nfcActionResult.postValue(nfcActionResult)
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
                _nfcActionResult.postValue(nfcActionResult)
            } catch (e: Exception) {
                Timber.e(e)

                val nfcActionResult = NfcActionResult.ErrorResult(
                    "${e.message}"
                )
                _nfcActionResult.postValue(nfcActionResult)
            } finally {
                closeConnection()

                this@NfcViewModel.nfcAction = null

                _nfcShowProgress.postValue(false)

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