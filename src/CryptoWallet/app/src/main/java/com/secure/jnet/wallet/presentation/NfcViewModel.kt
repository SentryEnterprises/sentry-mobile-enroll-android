package com.secure.jnet.wallet.presentation

import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.jcwkit.JCWIOException
import com.secure.jnet.jcwkit.JCWKit
import com.secure.jnet.jcwkit.SmartCardApduCallback
import com.secure.jnet.wallet.data.JCWCardWallet
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.util.SingleLiveEvent
import com.secure.jnet.wallet.util.byteArrayToHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class NfcViewModel @Inject constructor() : ViewModel() {

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

    private val jcwCardWallet by lazy { JCWCardWallet(callBack, JCWKit()) }

    private var nfcAction: NfcAction? = null

    @Volatile
    private var inProcess = false


    fun onTagDiscovered(tag: Tag?) {
        Timber.d("----> onTagDiscovered() $tag")


        this@NfcViewModel.tag = tag

        nfcAction?.let {
            Timber.d("----> Starting Action")
            startCardExchange(it)
        }
    }

    fun startNfcAction(nfcAction: NfcAction) {
        Timber.d("----> startNfcAction() $nfcAction")

        this@NfcViewModel.nfcAction = nfcAction

        startCardExchange(nfcAction)
    }

    private fun startCardExchange(nfcAction: NfcAction) {
        if (tag == null) return
        if (inProcess) return

        if (!openConnection()) return

         inProcess = true

        _nfcShowProgress.postValue(true)

        thread {
            try {
                val nfcActionResult: NfcActionResult = when (nfcAction) {

                    is NfcAction.GetEnrollmentStatus -> {
                        val status = jcwCardWallet.getEnrollmentStatus(nfcAction.pinCode)

                        Timber.d("-----> getEnrollmentStatus = $status")

                        status
                    }

                    is NfcAction.BiometricEnrollment -> {
                        val enrollFinger = jcwCardWallet.enrollFinger { progress ->
                            _nfcBiometricProgress.postValue(progress)
                        }
                        Timber.d("-----> enrollFinger = $enrollFinger")
                        enrollFinger
                    }

                    is NfcAction.VerifyBiometric -> {
                        val verifyBiometric = jcwCardWallet.verifyBiometric()
                        Timber.d("-----> verifyBiometric = $verifyBiometric")
                        verifyBiometric
                    }
                }

                _nfcActionResult.postValue(nfcActionResult)
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
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    private fun closeConnection() {
        try {
            mIsoDep?.close()
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            mIsoDep = null
        }
    }

    private fun ByteArray.logCommand() {
        Timber.d("---------------------------")
        Timber.d("=> ${
            byteArrayToHexString(this)
                .chunked(2).joinToString(separator = " ")
        }")
    }

    private fun ByteArray.logResponse() {
        Timber.d("<= ${
            byteArrayToHexString(this)
                .chunked(2).joinToString(separator = " ")
        }")
        Timber.d("---------------------------\n")
    }
}