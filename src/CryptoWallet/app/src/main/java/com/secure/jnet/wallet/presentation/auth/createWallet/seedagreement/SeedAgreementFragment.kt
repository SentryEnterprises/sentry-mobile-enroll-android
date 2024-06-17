//package com.secure.jnet.wallet.presentation.auth.createWallet.seedagreement
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentSeedAgreementBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SeedAgreementFragment : BaseFragment<FragmentSeedAgreementBinding>(
//    R.layout.fragment_seed_agreement
//) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnContinue.setOnClickListener {
//                navigateToSeedShowPhraseScreen()
//            }
//
//            llStatement1.setOnClickListener {
//                cbStatement1.isChecked = !cbStatement1.isChecked
//                checkStatements()
//            }
//
//            llStatement2.setOnClickListener {
//                cbStatement2.isChecked = !cbStatement2.isChecked
//                checkStatements()
//            }
//
//            llStatement3.setOnClickListener {
//                cbStatement3.isChecked = !cbStatement3.isChecked
//                checkStatements()
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        checkStatements()
//    }
//
//    private fun checkStatements() {
//        binding.apply {
//            btnContinue.isEnabled =
//                cbStatement1.isChecked && cbStatement2.isChecked && cbStatement3.isChecked
//        }
//    }
//
//    private fun navigateToSeedShowPhraseScreen() {
//        findNavController().navigate(
//            SeedAgreementFragmentDirections.actionSeedAgreementFragmentToSeedShowPhraseFragment()
//        )
//    }
//}