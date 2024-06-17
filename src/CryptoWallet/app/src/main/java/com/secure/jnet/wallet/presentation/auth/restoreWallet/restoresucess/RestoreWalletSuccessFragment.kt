//package com.secure.jnet.wallet.presentation.auth.restoreWallet.restoresucess
//
//import android.os.Bundle
//import android.view.View
//import androidx.activity.OnBackPressedCallback
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentRestoreWalletSuccessBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class RestoreWalletSuccessFragment : BaseFragment<FragmentRestoreWalletSuccessBinding>(
//    R.layout.fragment_restore_wallet_success
//) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            btnContinue.setOnClickListener {
//                navigateToHomeScreen()
//            }
//        }
//
//        requireActivity().onBackPressedDispatcher.addCallback(
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    // Prevent back navigation
//                }
//            },
//        )
//    }
//
//    private fun navigateToHomeScreen() {
////        findNavController().navigate(
////            RestoreWalletSuccessFragmentDirections.actionRestoreWalletSuccessFragmentToHomeFragment()
////        )
//    }
//}