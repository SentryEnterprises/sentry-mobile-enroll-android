package com.secure.jnet.wallet.presentation.home.menu.changepin

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.databinding.FragmentChangePinSuccessBinding
import com.secure.jnet.wallet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePinSuccessFragment :
    BaseFragment<FragmentChangePinSuccessBinding>(R.layout.fragment_change_pin_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnDone.setOnClickListener {
                navigateToSettingsScreen()
            }
        }
    }

    private fun navigateToSettingsScreen() {
        findNavController().popBackStack()
    }
}