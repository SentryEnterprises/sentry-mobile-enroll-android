package com.secure.jnet.wallet.presentation.auth.biometric.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import androidx.fragment.app.Fragment
import com.secure.jnet.wallet.databinding.FragmentBiometricTutorialBinding

class BiometricTutorialFragment : Fragment(
    R.layout.fragment_biometric_tutorial
) {
    private lateinit var binding: FragmentBiometricTutorialBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_biometric_tutorial, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

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