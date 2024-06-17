package com.secure.jnet.wallet.presentation.home.menu.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.databinding.FragmentSettingsBinding
import com.secure.jnet.wallet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivBack.setOnClickListener { findNavController().popBackStack() }

        //    btnResetWallet.setOnClickListener { navigateToResetWalletScreen() }

            viewContainer.isVisible = true
        }
    }

//    private fun navigateToResetWalletScreen() {
//        findNavController().navigate(
//            SettingsFragmentDirections.actionSettingsFragmentToResetWalletFragment()
//        )
//    }
//
//    private fun navigateToChangePinScreen() {
//        findNavController().navigate(
//            SettingsFragmentDirections.actionSettingsFragmentToChangePinFragment()
//        )
//    }
}