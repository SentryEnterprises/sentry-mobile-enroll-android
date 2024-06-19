package com.secure.jnet.wallet.presentation.cardState

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.databinding.FragmentGetCardStateBinding
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.presentation.base.BaseFragment
import com.secure.jnet.wallet.presentation.view.pin.PinView
import com.secure.jnet.wallet.util.BIOMETRIC_MODE
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import com.secure.jnet.wallet.util.ext.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetCardStateFragment : BaseFragment<FragmentGetCardStateBinding>(
    R.layout.fragment_get_card_state
), PinView.PinListener {

    private val viewModel: GetCardStateViewModel by viewModels()

    private val nfcViewModel: NfcViewModel by activityViewModels()

    private var pinEntered = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
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

        observe(viewModel.showEnrollmentStatus) {
            showEnrollmentStatus(it)
        }
        observe(viewModel.showPinView) {
            showPinView()
        }
    }

    private fun showPinView() {
        binding.apply {
            viewBiometricCard.isVisible = false
            viewPinPad.isVisible = true

            pinKeyboard.randomizeKeyboard()

            pinView.setupWithKeyboard(pinKeyboard)
            pinView.setPinListener(this@GetCardStateFragment)
        }
    }

    override fun onPinEntered(pinCode: String) {
         nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(pinCode))

        // hide pin pad, show card animation
        binding.viewBiometricCard.isVisible = true
        binding.viewPinPad.isVisible = false

        pinEntered = true
    }

    private fun showEnrollmentStatus(isEnrolled: Boolean) {
        val title = when(isEnrolled) {
            true -> getString(R.string.enroll_status_enrolled_title)
            false -> getString(R.string.enroll_status_notenrolled_title)
        }

        val message = when(isEnrolled) {
            true -> getString(R.string.enroll_status_enrolled_message)
            false -> getString(R.string.enroll_status_notenrolled_message)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.global_ok) { dialog, _ ->
                dialog.dismiss()

                if (isEnrolled) {
                    navigateToVerifyScreen()
                } else {
                    navigateToEnrollmentScreen()
                }
            }
            .create()
            .show()
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.card_error_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.try_again) { dialog, _ ->
                dialog.dismiss()

                if (BIOMETRIC_MODE) {
                    nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
                }
            }
            .create()
            .show()
    }

    private fun navigateToEnrollmentScreen() {
        findNavController().navigate(
            GetCardStateFragmentDirections.actionAttachCardFragmentToBiometricTutorialFragment()
        )
    }

    private fun navigateToVerifyScreen() {
        findNavController().navigate(
            GetCardStateFragmentDirections.actionAttachCardFragmentToBiometricVerifyFragment()
        )
    }
}