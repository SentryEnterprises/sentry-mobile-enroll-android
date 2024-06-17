//package com.secure.jnet.wallet.presentation.auth.createWallet.seedtutorial
//
//import android.os.Bundle
//import android.view.View
//import androidx.activity.OnBackPressedCallback
//import androidx.core.view.isVisible
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentSeedTutorialBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SeedTutorialFragment : BaseFragment<FragmentSeedTutorialBinding>(
//    R.layout.fragment_seed_tutorial
//) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            btnStart.setOnClickListener {
//                navigateToSeedAgreementScreen()
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
//    private fun navigateToSeedAgreementScreen() {
//        findNavController().navigate(
//            SeedTutorialFragmentDirections.actionSeedTutorialFragmentToSeedAgreementFragment()
//        )
//    }
//}