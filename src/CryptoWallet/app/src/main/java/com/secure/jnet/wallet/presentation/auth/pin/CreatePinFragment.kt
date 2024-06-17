package com.secure.jnet.wallet.presentation.auth.pin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.databinding.FragmentCreatePinBinding
import com.secure.jnet.wallet.presentation.base.BaseFragment
import com.secure.jnet.wallet.presentation.view.pin.PinView
import com.secure.jnet.wallet.util.ext.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePinFragment : BaseFragment<FragmentCreatePinBinding>(
    R.layout.fragment_create_pin
), PinView.PinListener {

    private val args by navArgs<CreatePinFragmentArgs>()
    private val mode by lazy { args.mode }

    private val viewModel: CreatePinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(mode)

        binding.apply {
            ivBack.setOnClickListener { findNavController().popBackStack() }

            pinKeyboard.setBiometricEnabled(false)
            pinKeyboard.randomizeKeyboard()

            pinView.setupWithKeyboard(binding.pinKeyboard)
            pinView.setPinListener(this@CreatePinFragment)
        }
    }

    override fun onBindLiveData() {
        observe(viewModel.showConfirmPinView) {
            binding.apply {
                tvAction.text = getString(R.string.create_pin_confirm)
                pinView.cleanPinView()
                pinKeyboard.randomizeKeyboard()
            }
        }

        observe(viewModel.showPinError) {
            binding.pinView.showError()
        }

        observe(viewModel.navigateToCreateWalletScreen) {
            navigateToCreateWalletScreen(it)
        }

        observe(viewModel.navigateToRestoreWalletScreen) {
            navigateToRestoreWalletScreen(it)
        }
    }

    override fun onPinEntered(pin: String) {
        viewModel.onPinEntered(pin)
    }

    private fun navigateToCreateWalletScreen(pinCode: String) {
        findNavController().navigate(
            CreatePinFragmentDirections.actionCreatePinFragmentToCreatingWalletFragment(pinCode)
        )
    }

    private fun navigateToRestoreWalletScreen(pinCode: String) {
        findNavController().navigate(
            CreatePinFragmentDirections.actionCreatePinFragmentToRecoverySeedFragment(pinCode)
        )
    }
}