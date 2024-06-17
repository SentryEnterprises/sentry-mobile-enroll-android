//package com.secure.jnet.wallet.presentation.home.send.details
//
//import android.content.ClipboardManager
//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.core.widget.doOnTextChanged
//import androidx.fragment.app.setFragmentResultListener
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentTransactionDetailsBinding
//import com.secure.jnet.wallet.domain.models.enums.getIcon
//import com.secure.jnet.wallet.presentation.base.BaseFragment
////import com.secure.jnet.wallet.presentation.home.main.HomeFragmentDirections
////import com.secure.jnet.wallet.presentation.home.scanqr.ScanQRCodeFragment
//import com.secure.jnet.wallet.presentation.home.send.SendTransactionViewModel
//import com.secure.jnet.wallet.presentation.models.Balance
//import com.secure.jnet.wallet.presentation.models.Fee
//import com.secure.jnet.wallet.presentation.view.AddressView
//import com.secure.jnet.wallet.util.ext.hideKeyboard
//import com.secure.jnet.wallet.util.ext.observe
//import com.secure.jnet.wallet.util.ext.onDone
//import com.secure.jnet.wallet.util.ext.placeCursorToEnd
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class TransactionDetailsFragment : BaseFragment<FragmentTransactionDetailsBinding>(
//    R.layout.fragment_transaction_details
//) {
//
//    private val viewModel: SendTransactionViewModel by hiltNavGraphViewModels(R.id.send_graph)
//
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////
//////        setFragmentResultListener(
//////            ScanQRCodeFragment.SCAN_QR_CODE_KEY
//////        ) { _: String, bundle: Bundle ->
////////            val qrCode = bundle.getString(ScanQRCodeFragment.QR_CODE_EXTRA)!!
////////            viewModel.onAddressChanged(qrCode)
//////        }
////    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            ivTokenIcon.setImageDrawable(
//                ContextCompat.getDrawable(
//                    ivTokenIcon.context,
//                    viewModel.cryptoCurrency.getIcon()
//                )
//            )
//
//            tvTokenName.text = viewModel.cryptoCurrency.name
//            tvTokenTicker.text = viewModel.cryptoCurrency.ticker
//
//            viewAddress.apply {
//                setOnClickListener(object : AddressView.OnAddressViewClickListener {
//                    override fun onPasteClicked() {
//                        viewAddress.clearFocus()
//                        pasteFromClipboard()
//                    }
//
//                    override fun onScanQRClicked() {
//                        viewAddress.clearFocus()
//                        navigateToScanQRCode()
//                    }
//
//                    override fun onClearClicked() {
//                        viewAddress.clearFocus()
//                        viewModel.onAddressChanged("")
//                    }
//                })
//            }
//
//            etTokenAmount.apply {
//                doOnTextChanged { text, _, _, _ ->
//                    if (etTokenAmount.hasFocus()) {
//                        viewModel.onTokenAmountChanged(text.toString())
//                    }
//                }
//
//                onDone {
//                    etTokenAmount.clearFocus()
//                    hideKeyboard()
//                }
//
//                placeCursorToEnd()
//            }
//
//            etFiatAmount.apply {
//                doOnTextChanged { text, _, _, _ ->
//                    if (etFiatAmount.hasFocus()) {
//                        viewModel.onFiatAmountChanged(text.toString())
//                    }
//                }
//
//                onDone {
//                    etFiatAmount.clearFocus()
//                    hideKeyboard()
//                }
//
//                placeCursorToEnd()
//            }
//
//            btnNext.setOnClickListener {
//                viewModel.buildTransaction()
//            }
//
//            btnMax.setOnClickListener {
//                hideKeyboard()
//                etFiatAmount.clearFocus()
//                etTokenAmount.clearFocus()
//
//                viewModel.onMaxClick()
//            }
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.showError) {
//            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//        }
//
//        observe(viewModel.balance) {
//            showBalance(it)
//        }
//
//        observe(viewModel.showAddress) {
//            binding.viewAddress.setAddress(it)
//        }
//
//        observe(viewModel.showInvalidAddress) {
//            binding.apply {
//                tvAddressError.isVisible = it
//                viewAddress.setAddressValid(!it)
//            }
//        }
//
//        observe(viewModel.amountToken) {
//            binding.etTokenAmount.setText(it)
//        }
//
//        observe(viewModel.amountFiat) {
//            binding.etFiatAmount.setText(it)
//        }
//
//        observe(viewModel.fee) {
//            binding.apply {
//                tvAmountError.isVisible = false
//                viewEstimatedFee.isVisible = true
//
//                showFee(it)
//            }
//        }
//
//        observe(viewModel.showAmountError) {
//            binding.apply {
//                tvAmountError.isVisible = true
//                viewEstimatedFee.isVisible = false
//
//                tvAmountError.text = it
//            }
//        }
//
//        observe(viewModel.nextButtonEnabled) {
//            binding.btnNext.isEnabled = it
//        }
//
//        observe(viewModel.navigateToConfirmTransactionScreen) {
//            navigateToConfirmScreen()
//        }
//    }
//
//    private fun pasteFromClipboard() {
//        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        clipboard.let {
//            val item = clipboard.primaryClip?.getItemAt(0)
//            if (item != null) {
//                val clipboardText = item.text.toString()
//                if (clipboardText.isNotEmpty()) {
//                    viewModel.onAddressChanged(clipboardText)
//                }
//            }
//        }
//    }
//
//    private fun showBalance(balance: Balance) {
//        binding.apply {
//            tvTokenBalance.text = balance.amountToken
//            tvTokenBalanceInFiat.text = balance.amountFiat
//        }
//    }
//
//    private fun showFee(fee: Fee) {
//        binding.apply {
//            tvEstimatedFee.text = fee.amountToken
//            tvEstimatedFeeFiat.text = fee.amountFiat
//        }
//    }
//
//    private fun navigateToScanQRCode() {
////        findNavController().navigate(
////            HomeFragmentDirections.actionGlobalScanQRCodeFragment()
////        )
//    }
//
//    private fun navigateToConfirmScreen() {
//        findNavController().navigate(
//            TransactionDetailsFragmentDirections
//                .actionSendTransactionFragmentToConfirmTransactionFragment()
//        )
//    }
//}