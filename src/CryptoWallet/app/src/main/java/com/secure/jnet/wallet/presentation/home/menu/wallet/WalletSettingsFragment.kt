//package com.secure.jnet.wallet.presentation.home.menu.wallet
//
//import android.os.Bundle
//import android.view.View
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentWalletSettingsBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class WalletSettingsFragment :
//    BaseFragment<FragmentWalletSettingsBinding>(R.layout.fragment_wallet_settings) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
//    }
//}