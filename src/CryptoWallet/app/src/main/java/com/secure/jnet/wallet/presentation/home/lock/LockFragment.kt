package com.secure.jnet.wallet.presentation.home.lock

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.util.observe


class LockFragment : Fragment(
    R.layout.fragment_lock
) {

    @SuppressLint("FragmentBackPressedCallback")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        observe(nfcViewModel.nfcActionResult) {
//            viewModel.processNfcActionResult(it)
//        }
//
//        observe(viewModel.showNfcError) {
//            showError(it)
//        }
//
//        observe(viewModel.isVerified) {
//            showVerified()
//        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.card_error_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.try_again) { dialog, _ ->
                dialog.dismiss()
//                nfcViewModel.startNfcAction(NfcAction.VerifyBiometric)
            }
            .create()
            .show()
    }

    private fun showVerified() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.global_success))
            .setMessage(getString(R.string.biometric_verification_success))
            .setCancelable(false)
            .setPositiveButton(R.string.global_ok) { dialog, _ ->
                dialog.dismiss()
//                nfcViewModel.startNfcAction(NfcAction.VerifyBiometric)
            }
            .create()
            .show()
    }
}