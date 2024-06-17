package com.secure.jnet.wallet.presentation.home.lock

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.databinding.FragmentLockBinding
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.presentation.base.BaseFragment
import com.secure.jnet.wallet.presentation.view.pin.PinView
import com.secure.jnet.wallet.util.BIOMETRIC_MODE
import com.secure.jnet.wallet.util.WORK_WITHOUT_CARD
import com.secure.jnet.wallet.util.ext.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LockFragment : BaseFragment<FragmentLockBinding>(
    R.layout.fragment_lock
), PinView.PinListener {

    private val viewModel: LockViewModel by viewModels()

    private val nfcViewModel: NfcViewModel by activityViewModels()

    @SuppressLint("FragmentBackPressedCallback")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (BIOMETRIC_MODE) {
                viewBiometricCard.isVisible = true
                viewPinPad.isVisible = false

                openVerifyBiometricsNfcActivity("")
            } else {
                viewBiometricCard.isVisible = false
                viewPinPad.isVisible = true

                pinKeyboard.randomizeKeyboard()

                pinView.setupWithKeyboard(pinKeyboard)
                pinView.setPinListener(this@LockFragment)
            }
        }

//        requireActivity().onBackPressedDispatcher.addCallback(
//            this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    // prevent user to use back button
//                }
//            },
//        )
    }

    override fun onBindLiveData() {
        observe(nfcViewModel.nfcShowProgress) {
            binding.progressContainer.isVisible = it
        }

        observe(nfcViewModel.nfcActionResult) {
            viewModel.processNfcActionResult(it)
        }

        observe(viewModel.showNfcError) {
            showError(it)
        }

        observe(viewModel.isVerified) {
            showVerified()
        }
    }

    override fun onPinEntered(pin: String) {
        openVerifyBiometricsNfcActivity(pin)

        // hide pin pad, show card animation
        binding.viewBiometricCard.isVisible = true
        binding.viewPinPad.isVisible = false
    }

    private fun openVerifyBiometricsNfcActivity(pinCode: String) {
//        if (WORK_WITHOUT_CARD) {
//            binding.root.setOnClickListener {
//                navigateBack()
//            }
//            return
//        }

        nfcViewModel.startNfcAction(NfcAction.VerifyBiometric)
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.card_error_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.try_again) { dialog, _ ->
                dialog.dismiss()

                if (BIOMETRIC_MODE) {
                    openVerifyBiometricsNfcActivity("")
                }
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

                if (BIOMETRIC_MODE) {
                    openVerifyBiometricsNfcActivity("")
                }
            }
            .create()
            .show()
    }

//    private fun navigateBack() {
//        findNavController().popBackStack()
//    }
}