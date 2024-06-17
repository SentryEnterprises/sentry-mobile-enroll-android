package com.secure.jnet.wallet.presentation.home.menu.changepin

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.databinding.FragmentChangePinBinding
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.presentation.base.BaseFragment
import com.secure.jnet.wallet.presentation.view.pin.PinView
import com.secure.jnet.wallet.util.ext.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePinFragment : BaseFragment<FragmentChangePinBinding>(R.layout.fragment_change_pin),
    PinView.PinListener {

    private val viewModel: ChangePinViewModel by viewModels()

    private val nfcViewModel: NfcViewModel by activityViewModels()

    private var currentPin = ""
    private var newPin = ""
    private var confirmPin = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivBack.setOnClickListener { findNavController().popBackStack() }

            pinKeyboard.setBiometricEnabled(false)
            pinKeyboard.randomizeKeyboard()

            pinView.setupWithKeyboard(binding.pinKeyboard)
            pinView.setPinListener(this@ChangePinFragment)
        }
    }

    override fun onBindLiveData() {
        observe(nfcViewModel.nfcShowProgress) {
            binding.progressContainer.isVisible = it
        }

        observe(nfcViewModel.nfcActionResult) {
            viewModel.processNfcActionResult(it)
        }

        observe(viewModel.showNfcError) {
            showError(it)
        }

        observe(viewModel.navigateToPinChangeSuccessScreen) {
            navigateToPinChangeSuccessScreen()
        }
    }

    override fun onPinEntered(pin: String) {
        processPin(pin)
    }

    private fun processPin(pin: String) {
        if (currentPin.isBlank()) {
            currentPin = pin
            binding.tvAction.text = getString(R.string.change_pin_enter_new)
            binding.pinView.cleanPinView()
        } else if (newPin.isBlank()) {
            newPin = pin
            binding.tvAction.text = getString(R.string.change_pin_confirm_new)
            binding.pinView.cleanPinView()
        } else {
            confirmPin = pin
            if (newPin == pin) {
                openChangePinCodeNfcActivity(currentPin, newPin)
            } else {
                binding.pinView.showError()
            }
        }
    }

    private fun openChangePinCodeNfcActivity(currentPinCode: String, newPinCode: String) {
        binding.apply {
            ivBack.isVisible = false
            viewPinPad.isVisible = false
            viewAttachCard.isVisible = true
        }

        nfcViewModel.startNfcAction(NfcAction.ChangePin(currentPinCode, newPinCode))
    }

    private fun showError(message: String) {
        binding.pinView.showError()

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.card_error_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.try_again) { dialog, _ ->
                dialog.dismiss()
                showIncorrectPinCode()
                resetChangePinProcess()
            }
            .create()
            .show()
    }

    private fun navigateToPinChangeSuccessScreen() {
        findNavController().navigate(
            ChangePinFragmentDirections
                .actionChangePinFragmentToChangePinSuccessFragment()
        )
    }

    private fun showIncorrectPinCode() {
        Toast.makeText(requireContext(), R.string.change_pin_error, Toast.LENGTH_LONG).show()
    }

    private fun resetChangePinProcess() {
        currentPin = ""
        newPin = ""
        confirmPin = ""
        binding.tvAction.text = getString(R.string.change_pin_enter)
        binding.pinView.cleanPinView()
    }
}