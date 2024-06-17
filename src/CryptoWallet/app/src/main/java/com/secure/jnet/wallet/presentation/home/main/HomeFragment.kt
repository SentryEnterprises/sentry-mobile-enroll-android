//package com.secure.jnet.wallet.presentation.home.main
//
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.view.View
//import android.view.WindowManager
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.fragment.app.setFragmentResultListener
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.tabs.TabLayout
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentHomeBinding
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.common.adapter.TokensAdapter
//import com.secure.jnet.wallet.presentation.common.adapter.TransactionItemDecoration
//import com.secure.jnet.wallet.presentation.common.adapter.TransactionsAdapter
//import com.secure.jnet.wallet.presentation.home.main.dialog.MoreActionsDialog
////import com.secure.jnet.wallet.presentation.home.scanqr.ScanQRCodeFragment
//import com.secure.jnet.wallet.presentation.models.TokenItem
//import com.secure.jnet.wallet.util.BlockchainUtils
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//import timber.log.Timber
//
//@AndroidEntryPoint
//class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
//
//    private val viewModel: HomeViewModel by viewModels()
//
//    private val tokensAdapter by lazy {
//        TokensAdapter {
//            onTokenClick(it)
//        }
//    }
//
//    private val transactionsAdapter by lazy {
//        TransactionsAdapter {
//            BlockchainUtils.openBlockchainExplorer(it.cryptoCurrency, requireContext(), it.hash)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setFragmentResultListener(
//            MoreActionsDialog.ACTION_KEY
//        ) { _: String, bundle: Bundle ->
//            when (
//                MoreActionsDialog.Action.valueOf(bundle.getString(MoreActionsDialog.ACTION_EXTRA)!!)
//            ) {
//                MoreActionsDialog.Action.ACTION_BUY -> {
//
//                }
//
//                MoreActionsDialog.Action.ACTION_CASH_OUT -> {
//
//                }
//
//                MoreActionsDialog.Action.ACTION_SWAP -> {
//                }
//            }
//        }
//
////        setFragmentResultListener(
////            ScanQRCodeFragment.SCAN_QR_CODE_KEY
////        ) { _: String, bundle: Bundle ->
////            val qrCode = bundle.getString(ScanQRCodeFragment.QR_CODE_EXTRA)
////            Timber.d("---> QR code is $qrCode")
////
////            Handler(Looper.getMainLooper()).postDelayed({
////                view?.post { navigateToSendScreen(qrCode) }
////            }, OPEN_QR_SCANNER_DELAY)
////        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            btnSettings.setOnClickListener { navigateToSettingsScreen() }
//
//            btnScanQR.setOnClickListener { showScanQRCode() }
//
//            btnReceive.setOnClickListener { navigateToReceiveScreen() }
//            btnSend.setOnClickListener { navigateToSendScreen(null) }
//            btnMore.setOnClickListener { showMoreActions() }
//
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    if (tab?.position == TOKENS_TAB_POSITION) {
//                        tokensViewContainer.isVisible = true
//                        activityViewContainer.isVisible = false
//
//                        rvMyTransactions.isVisible = false
//                        ivNoActivity.isVisible = false
//                        tvNoActivity.isVisible = false
//
//                        viewModel.onTokenTabClick()
//                    } else if (tab?.position == ACTIVITY_TAB_POSITION) {
//                        tokensViewContainer.isVisible = false
//                        activityViewContainer.isVisible = true
//
//                        rvMyTokens.isVisible = false
//
//                        viewModel.onActivityTabClick()
//                    }
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {}
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {}
//            })
//
//            rvMyTokens.apply {
//                adapter = tokensAdapter
//                addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
//            }
//
//            rvMyTransactions.apply {
//                adapter = transactionsAdapter
//                addItemDecoration(
//                    TransactionItemDecoration(
//                        ContextCompat.getDrawable(
//                            requireContext(),
//                            R.drawable.divider
//                        )!!,
//                        TransactionsAdapter.TYPE_DATA
//                    )
//                )
//            }
//
//            root.setBackgroundResource(R.color.color_background)
//
//            ivLogo.isVisible = true
//
//            btnReceive.isVisible = true
//            btnSend.isVisible = true
//            btnMore.isVisible = true
//
//            tvWallet.isVisible = true
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.totalBalance) {
//            binding.tvBalance.text = it
//        }
//
//        observe(viewModel.showTokensProgress) {
//            binding.apply {
//                progressTokens.isVisible = it
//                rvMyTokens.isVisible = !it
//
//                progressBalance.isVisible = it
//                tvBalance.isVisible = !it
//            }
//        }
//
//        observe(viewModel.myTokens) {
//            tokensAdapter.submitList(it)
//        }
//
//        observe(viewModel.showTransactionsProgress) {
//            binding.apply {
//                progressTransactions.isVisible = it
//            }
//        }
//
//        observe(viewModel.transactions) {
//            transactionsAdapter.submitList(it)
//
//            binding.apply {
//                rvMyTransactions.isVisible = it.isNotEmpty()
//                ivNoActivity.isVisible = it.isEmpty()
//                tvNoActivity.isVisible = it.isEmpty()
//            }
//        }
//    }
//
//    override fun onResume() {
//        requireActivity().window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )
//        super.onResume()
//        viewModel.onResume()
//    }
//
//    override fun onPause() {
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
//        super.onPause()
//    }
//
//    private fun showScanQRCode() {
////        findNavController().navigate(
////            HomeFragmentDirections.actionGlobalScanQRCodeFragment()
////        )
//    }
//
//    private fun showMoreActions() {
//        val dialog = MoreActionsDialog.newInstance()
//        dialog.show(parentFragmentManager, MoreActionsDialog.TAG)
//    }
//
//    private fun onTokenClick(myTokenData: TokenItem.TokenData) {
//        navigateToTokenDetailsScreen(myTokenData.cryptoCurrency)
//    }
//
//    private fun navigateToReceiveScreen() {
//        findNavController().navigate(
//            HomeFragmentDirections.actionHomeFragmentToReceiveFragment()
//        )
//    }
//
//    private fun navigateToSendScreen(address: String?) {
//        findNavController().navigate(
//            HomeFragmentDirections.actionHomeFragmentToSendFragment(
//                address = address,
//                cryptoCurrencyName = null
//            )
//        )
//    }
//
//    private fun navigateToSettingsScreen() {
//        findNavController().navigate(
//            HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
//        )
//    }
//
//    private fun navigateToTokenDetailsScreen(cryptoCurrency: CryptoCurrency) {
//        findNavController().navigate(
//            HomeFragmentDirections.actionHomeFragmentToTokenDetailsFragment(cryptoCurrency)
//        )
//    }
//
//    private companion object {
//        private const val TOKENS_TAB_POSITION = 0
//        private const val ACTIVITY_TAB_POSITION = 1
//
//        private const val OPEN_QR_SCANNER_DELAY = 500L
//    }
//}