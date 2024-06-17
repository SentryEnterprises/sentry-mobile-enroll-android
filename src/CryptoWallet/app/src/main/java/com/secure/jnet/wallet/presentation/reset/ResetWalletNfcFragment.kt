//package com.secure.jnet.wallet.presentation.reset
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.data.nfc.NfcAction
//import com.secure.jnet.wallet.databinding.FragmentResetWalletNfcBinding
//import com.secure.jnet.wallet.presentation.NfcViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ResetWalletNfcFragment :
//    BaseFragment<FragmentResetWalletNfcBinding>(R.layout.fragment_reset_wallet_nfc) {
//
//    private val viewModel: ResetWalletViewModel by viewModels()
//
//    private val nfcViewModel: NfcViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//        }
//
//        openResetWalletNfcActivity()
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
//        observe(viewModel.navigateToCardStateScreen) {
//            navigateToCardStateScreen()
//        }
//    }
//
//    private fun openResetWalletNfcActivity() {
//        nfcViewModel.startNfcAction(NfcAction.ResetWallet)
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
//                openResetWalletNfcActivity()
//            }
//            .create()
//            .show()
//    }
//
//    private fun navigateToCardStateScreen() {
////        findNavController().navigate(
////            ResetWalletNfcFragmentDirections.actionResetWalletNfcFragmentToAttachCardFragment(
////                true
////            )
////        )
//    }
//}