//package com.secure.jnet.wallet.presentation.auth.biometric.begin
//
//import android.os.Bundle
//import android.view.View
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentBiometricBeginBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.auth.biometric.BiometricViewModel
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class BiometricBeginFragment : BaseFragment<FragmentBiometricBeginBinding>(
//    R.layout.fragment_biometric_begin
//) {
//
//    private val viewModel: BiometricViewModel by hiltNavGraphViewModels(R.id.biometric_graph)
//
//    private val args by navArgs<BiometricBeginFragmentArgs>()
//    private val mode by lazy { args.mode }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel.mode = mode
//
//        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
//
//        binding.btnDescription.setOnClickListener {
//            navigateToBiometricDescriptionScreen()
//        }
//
//        binding.btnBegin.setOnClickListener {
//            navigateToBiometricTutorialScreen()
//        }
//    }
//
//    private fun navigateToBiometricDescriptionScreen() {
//        findNavController().navigate(
//            BiometricBeginFragmentDirections
//                .actionBiometricBeginFragmentToBiometricDescriptionFragment()
//        )
//    }
//
//    private fun navigateToBiometricTutorialScreen() {
//        findNavController().navigate(
//            BiometricBeginFragmentDirections
//                .actionBiometricBeginFragmentToBiometricTutorialFragment()
//        )
//    }
//}