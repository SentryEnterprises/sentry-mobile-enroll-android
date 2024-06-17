//package com.secure.jnet.wallet.presentation.home.receive.details
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentReceiveDetailsBinding
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.QRUtil
//import com.secure.jnet.wallet.util.ext.copyToClipboard
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//import okhttp3.internal.format
//
//@AndroidEntryPoint
//class ReceiveDetailsFragment : BaseFragment<FragmentReceiveDetailsBinding>(
//    R.layout.fragment_receive_details
//) {
//
//    private val viewModel: ReceiveDetailsViewModel by viewModels()
//
//    private val args by navArgs<ReceiveDetailsFragmentArgs>()
//    private val cryptoCurrency: CryptoCurrency by lazy { args.cryptoCurrency }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        viewModel.init(cryptoCurrency)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnShare.setOnClickListener {
//                viewModel.onShareAddressClicked()
//            }
//
//            btnCopy.setOnClickListener {
//                viewModel.onCopyAddressToClipboardClicked()
//            }
//
//            tvTitle.text = format(getString(R.string.receive_title_ticker), cryptoCurrency.ticker)
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.address) {
//            binding.tvAddress.text = it
//
//            val qr = QRUtil.generateQR(requireContext(), it)
//            binding.ivQr.setImageBitmap(qr)
//        }
//
//        observe(viewModel.copyAddressToClipboard) {
//            copyAddressToClipBoard(it)
//
//            Toast.makeText(requireContext(), R.string.receive_details_copied, Toast.LENGTH_SHORT)
//                .show()
//        }
//
//        observe(viewModel.shareAddress) {
//            shareWalletAddress(it)
//        }
//    }
//
//    private fun copyAddressToClipBoard(address: String) {
//        requireContext().copyToClipboard(address)
//    }
//
//    private fun shareWalletAddress(address: String) {
//        val shareIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_STREAM, address)
//            type = "text/plain"
//        }
//        startActivity(Intent.createChooser(shareIntent, null))
//    }
//}