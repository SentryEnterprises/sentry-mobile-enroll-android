//package com.secure.jnet.wallet.presentation.home.send.confirm
//
//import android.os.Bundle
//import android.view.View
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentConfirmTransactionBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.home.send.SendTransactionViewModel
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ConfirmTransactionFragment : BaseFragment<FragmentConfirmTransactionBinding>(
//    R.layout.fragment_confirm_transaction
//) {
//
//    private val viewModel: SendTransactionViewModel by hiltNavGraphViewModels(R.id.send_graph)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnSend.setOnClickListener {
//                navigateToSignTransactionScreen()
//            }
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.showAddress) {
//            binding.tvToAddress.text = it
//        }
//
//        observe(viewModel.transactionDetails) {
//            binding.apply {
//                tvAmount.text = it.amountToken
//                tvAmountInFiat.text = it.amountFiat
//                tvFee.text = it.feeToken
//                tvFeeInFiat.text = it.feeFiat
//                tvTotalAmount.text = it.totalAmountToken
//                tvTotalAmountInFiat.text = it.totalAmountFiat
//            }
//        }
//    }
//
//    private fun navigateToSignTransactionScreen() {
//        findNavController().navigate(
//            ConfirmTransactionFragmentDirections
//                .actionConfirmTransactionFragmentToSignTransactionFragment()
//        )
//    }
//}