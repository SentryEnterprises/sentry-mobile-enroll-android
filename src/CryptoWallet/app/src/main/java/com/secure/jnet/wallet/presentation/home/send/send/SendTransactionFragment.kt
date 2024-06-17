//package com.secure.jnet.wallet.presentation.home.send.send
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.View
//import androidx.activity.OnBackPressedCallback
//import androidx.core.view.isVisible
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentSendTransactionBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.home.send.SendTransactionViewModel
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SendTransactionFragment : BaseFragment<FragmentSendTransactionBinding>(
//    R.layout.fragment_send_transaction
//) {
//
//    private val viewModel: SendTransactionViewModel by hiltNavGraphViewModels(R.id.send_graph)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            btnDone.setOnClickListener {
//                navigateToHomeScreen()
//            }
//
//            btnTryAgain.setOnClickListener {
//                navigateToConfirmScreen()
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
//
//        viewModel.sendTransaction()
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindLiveData() {
//        observe(viewModel.showProgress) {
//            showProgress(it)
//        }
//
//        observe(viewModel.sendTransactionSuccess) {
//            binding.apply {
//                tvSentInfo.text = it
//
//                viewSuccess.isVisible = true
//                btnDone.isVisible = true
//            }
//        }
//
//        observe(viewModel.sendTransactionError) {
//            binding.apply {
//                viewError.isVisible = true
//                btnTryAgain.isVisible = true
//            }
//        }
//    }
//
//    private fun showProgress(show: Boolean) {
//        binding.progressContainer.isVisible = show
//    }
//
//    private fun navigateToConfirmScreen() {
//        findNavController().navigate(
//            SendTransactionFragmentDirections.actionSentFragmentToConfirmTransactionFragment()
//        )
//    }
//
//    private fun navigateToHomeScreen() {
////        findNavController().navigate(
////            SendTransactionFragmentDirections.actionSentFragmentToHomeFragment()
////        )
//    }
//}