package com.secure.jnet.wallet.presentation.auth.biometric.tutorial

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.databinding.FragmentBiometricTutorialBinding
import com.secure.jnet.wallet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiometricTutorialFragment : BaseFragment<FragmentBiometricTutorialBinding>(
    R.layout.fragment_biometric_tutorial
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnFinish.setOnClickListener {
            navigateToEnrollFingerScreen()
        }
    }

    private fun navigateToEnrollFingerScreen() {
        findNavController().navigate(
            BiometricTutorialFragmentDirections.actionBiometricTutorialFragmentToBiometricFingerEnrollFragment()
        )
    }
}