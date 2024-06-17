//package com.secure.jnet.wallet.presentation.auth.createWallet.signupsuccess
//
//import android.os.Bundle
//import android.view.View
//import android.view.WindowManager
//import androidx.activity.OnBackPressedCallback
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentSeedDoneBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SeedDoneFragment : BaseFragment<FragmentSeedDoneBinding>(
//    R.layout.fragment_seed_done
//) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
//
//        binding.apply {
//            btnContinue.setOnClickListener {
////                findNavController().navigate(
////                    SeedDoneFragmentDirections.actionSeedDoneFragmentToHomeFragment()
////                )
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
//}