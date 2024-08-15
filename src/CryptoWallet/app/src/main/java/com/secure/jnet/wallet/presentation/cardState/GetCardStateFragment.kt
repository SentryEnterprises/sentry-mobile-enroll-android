package com.secure.jnet.wallet.presentation.cardState

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import com.secure.jnet.wallet.util.observe

class GetCardStateFragment : Fragment(
    R.layout.fragment_get_card_state
) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        observe(viewModel.showEnrollmentStatus) {
//            showEnrollmentStatus(it)
//        }
//
//        nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
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

//                if (isEnrolled) {
//                    navigateToVerifyScreen()
//                } else {
//                    navigateToEnrollmentScreen()
//                }
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
//                nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
            }
            .create()
            .show()
    }
//
//    private fun navigateToEnrollmentScreen() {
//        findNavController().navigate(
//            GetCardStateFragmentDirections.actionAttachCardFragmentToBiometricTutorialFragment()
//        )
//    }
//
//    private fun navigateToVerifyScreen() {
//        findNavController().navigate(
//            GetCardStateFragmentDirections.actionAttachCardFragmentToBiometricVerifyFragment()
//        )
//    }
}