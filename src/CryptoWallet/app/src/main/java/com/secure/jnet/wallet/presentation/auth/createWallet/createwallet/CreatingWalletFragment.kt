//package com.secure.jnet.wallet.presentation.auth.createWallet.createwallet
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
//import com.secure.jnet.wallet.presentation.NfcViewModel
//import com.secure.jnet.wallet.presentation.auth.createWallet.CreateWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class CreatingWalletFragment : BaseFragment<FragmentTapCardBinding>(
//    R.layout.fragment_tap_card
//) {
//
//    private val viewModel: CreateWalletViewModel by activityViewModels()
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
//        openCreateWalletNfcActivity(viewModel.pinCode)
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
//        observe(viewModel.navigateToProtectWalletScreen) {
//            navigateToProtectWalletScreen()
//        }
//    }
//
//    private fun openCreateWalletNfcActivity(pinCode: String) {
//        nfcViewModel.startNfcAction(NfcAction.CreateWallet(pinCode, viewModel.wordsCount))
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
//                openCreateWalletNfcActivity(viewModel.pinCode)
//            }
//            .create()
//            .show()
//    }
//
//    private fun navigateToProtectWalletScreen() {
//        findNavController().navigate(
//            CreatingWalletFragmentDirections.actionCreatingWalletFragmentToSeedTutorialFragment()
//        )
//    }
//}