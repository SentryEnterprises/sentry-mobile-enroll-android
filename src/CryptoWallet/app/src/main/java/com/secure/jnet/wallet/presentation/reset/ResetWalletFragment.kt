//package com.secure.jnet.wallet.presentation.reset
//
//import android.os.Bundle
//import android.view.View
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentResetWalletBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ResetWalletFragment : BaseFragment<FragmentResetWalletBinding>(R.layout.fragment_reset_wallet) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            llSeedSaved.setOnClickListener {
//                cbSeedSaved.isChecked = !cbSeedSaved.isChecked
//
//                btnReset.isEnabled = cbSeedSaved.isChecked
//            }
//
//            btnReset.setOnClickListener {
//                navigateToResetWalletNfcScreen()
//            }
//
//            btnReset.isEnabled = cbSeedSaved.isChecked
//        }
//    }
//
//    private fun navigateToResetWalletNfcScreen() {
//        findNavController().navigate(
//            ResetWalletFragmentDirections.actionResetWalletFragmentToResetWalletNfcFragment()
//        )
//    }
//}