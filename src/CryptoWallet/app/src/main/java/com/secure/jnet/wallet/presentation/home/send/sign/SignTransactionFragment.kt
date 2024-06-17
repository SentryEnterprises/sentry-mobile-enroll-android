//package com.secure.jnet.wallet.presentation.home.send.sign
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.fragment.app.activityViewModels
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionDTO
//import com.secure.jnet.wallet.data.nfc.NfcAction
//import com.secure.jnet.wallet.databinding.FragmentSignTransactionBinding
//import com.secure.jnet.wallet.presentation.NfcViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.home.send.SendTransactionViewModel
//import com.secure.jnet.wallet.presentation.view.pin.PinView
//import com.secure.jnet.wallet.util.BIOMETRIC_MODE
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SignTransactionFragment : BaseFragment<FragmentSignTransactionBinding>(
//    R.layout.fragment_sign_transaction
//), PinView.PinListener {
//
//    private val viewModel: SendTransactionViewModel by hiltNavGraphViewModels(R.id.send_graph)
//
//    private val nfcViewModel: NfcViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            pinKeyboard.randomizeKeyboard()
//
//            pinView.setupWithKeyboard(binding.pinKeyboard)
//            pinView.setPinListener(this@SignTransactionFragment)
//
//            if (BIOMETRIC_MODE) {
//                viewBiometricCard.isVisible = true
//                viewPinPad.isVisible = false
//
//                openSignTransactionNfcActivity(viewModel.rawTransaction, "")
//            } else {
//                viewBiometricCard.isVisible = false
//                viewPinPad.isVisible = true
//            }
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(nfcViewModel.nfcShowProgress) {
//            binding.progressContainer.isVisible = it
//        }
//
//        observe(nfcViewModel.nfcActionResult) {
//            viewModel.processNfcActionResult(it)
//        }
//
//        observe(viewModel.showNfcError) {
//            showError(it)
//        }
//
//        observe(viewModel.navigateToSendTransactionScreen) {
//            navigateToSendTransactionScreen()
//        }
//    }
//
//    override fun onPinEntered(pin: String) {
//        openSignTransactionNfcActivity(viewModel.rawTransaction, pin)
//
//        // hide pin pad, show card animation
//        binding.viewBiometricCard.isVisible = true
//        binding.viewPinPad.isVisible = false
//    }
//
//    private fun openSignTransactionNfcActivity(transaction: RawTransactionDTO, pinCode: String) {
//        nfcViewModel.startNfcAction(NfcAction.SignTransaction(transaction, pinCode))
//    }
//
//    private fun showError(message: String) {
//        AlertDialog.Builder(requireContext())
//            .setTitle(getString(R.string.card_error_title))
//            .setMessage(message)
//            .setCancelable(false)
//            .setPositiveButton(R.string.try_again) { dialog, _ ->
//                if (BIOMETRIC_MODE) {
//                    openSignTransactionNfcActivity(viewModel.rawTransaction, "")
//                }
//
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }
//
//    private fun navigateToSendTransactionScreen() {
//        findNavController().navigate(
//            SignTransactionFragmentDirections.actionSignTransactionFragmentToSentFragment()
//        )
//    }
//}