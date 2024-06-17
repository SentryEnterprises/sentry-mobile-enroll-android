//package com.secure.jnet.wallet.presentation.auth.restoreWallet.restorewallet
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.View
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.data.nfc.NfcAction
//import com.secure.jnet.wallet.databinding.FragmentTapCardBinding
//import com.secure.jnet.wallet.presentation.auth.restoreWallet.RestoreWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.NfcViewModel
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class RestoreWalletFragment : BaseFragment<FragmentTapCardBinding>(
//    R.layout.fragment_tap_card
//) {
//
//    private val viewModel: RestoreWalletViewModel by activityViewModels()
//
//    private val nfcViewModel: NfcViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            tvProgress.isVisible = true
//        }
//
//        openRestoreWalletNfcActivity(viewModel.pinCode, viewModel.seedPhrase)
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
//        observe(nfcViewModel.nfcProgress) {
//            updateProgress(it)
//        }
//
//        observe(viewModel.showNfcError) {
//            showError(it)
//        }
//
//        observe(viewModel.navigateToRestoreWalletSuccessScreen) {
//            navigateToRestoreWalletSuccessScreen()
//        }
//    }
//
//    private fun openRestoreWalletNfcActivity(pinCode: String, mnemonicWords: String) {
//        nfcViewModel.startNfcAction(NfcAction.RestoreWallet(pinCode, mnemonicWords))
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun updateProgress(progress: Int) {
//        binding.tvProgress.text = "$progress%"
//    }
//
//    private fun showError(message: String) {
//        AlertDialog.Builder(requireContext())
//            .setTitle(getString(R.string.card_error_title))
//            .setMessage(message)
//            .setCancelable(false)
//            .setPositiveButton(R.string.try_again) { dialog, _ ->
//                dialog.dismiss()
//
//                openRestoreWalletNfcActivity(viewModel.pinCode, viewModel.seedPhrase)
//            }
//            .create()
//            .show()
//    }
//
//    private fun navigateToRestoreWalletSuccessScreen() {
//        findNavController().navigate(
//            RestoreWalletFragmentDirections
//                .actionRestoreWalletFragmentToRestoreWalletSuccessFragment()
//        )
//    }
//}