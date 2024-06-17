//package com.secure.jnet.wallet.presentation.auth.biometric.description
//
//import android.os.Bundle
//import android.view.View
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentBiometricDescriptionBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class BiometricDescriptionFragment : BaseFragment<FragmentBiometricDescriptionBinding>(
//    R.layout.fragment_biometric_description
//) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
//
//        binding.btnContinue.setOnClickListener {
//            navigateToBiometricTutorialScreen()
//        }
//    }
//
//    private fun navigateToBiometricTutorialScreen() {
//        findNavController().navigate(
//            BiometricDescriptionFragmentDirections
//                .actionBiometricDescriptionFragmentToBiometricTutorialFragment()
//        )
//    }
//}