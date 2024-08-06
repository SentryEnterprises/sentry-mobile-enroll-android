package com.secure.jnet.wallet.presentation.auth.biometric.enroll

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.data.nfc.NfcAction
import com.secure.jnet.wallet.databinding.FragmentBiometricFingerEnrollBinding
import com.secure.jnet.wallet.presentation.NfcViewModel
import com.secure.jnet.wallet.presentation.auth.biometric.BiometricViewModel
import com.secure.jnet.wallet.util.observe

class BiometricFingerEnrollFragment : Fragment(
    R.layout.fragment_biometric_finger_enroll
) {

    private val viewModel: BiometricViewModel = BiometricViewModel()

    private val nfcViewModel: NfcViewModel by activityViewModels()

    private lateinit var binding: FragmentBiometricFingerEnrollBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_biometric_finger_enroll, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivBack.setOnClickListener { findNavController().popBackStack() }

            btnFinish.setOnClickListener {
                navigateToBiometricDoneScreen()
            }
        }

//        observe(nfcViewModel.nfcShowProgress) {
//            binding.progressContainer.isVisible = it
//        }

        observe(nfcViewModel.nfcBiometricProgress) {
            updateBiometricProgress(it)
        }

//        observe(nfcViewModel.nfcActionResult) {
//            viewModel.processNfcActionResult(it)
//        }

        observe(viewModel.showNfcError) {
            showError(it)
        }

        observe(viewModel.showBiometricEnrollError) {
            showError(getString(R.string.biometric_enroll_failed))
        }

        observe(viewModel.showButtonContainer) {
            binding.btnContainer.isVisible = true
        }

        startFingerprintEnroll()
    }

//    override fun onBindLiveData() {
//        observe(nfcViewModel.nfcShowProgress) {
//            binding.progressContainer.isVisible = it
//        }
//
//        observe(nfcViewModel.nfcBiometricProgress) {
//            updateBiometricProgress(it)
//        }
//
//        observe(nfcViewModel.nfcActionResult) {
//            viewModel.processNfcActionResult(it)
//        }
//
//        observe(viewModel.showNfcError) {
//            showError(it)
//        }
//
//        observe(viewModel.showBiometricEnrollError) {
//            showError(getString(R.string.biometric_enroll_failed))
//        }
//
//        observe(viewModel.showButtonContainer) {
//            binding.btnContainer.isVisible = true
//        }
//    }

    private fun startFingerprintEnroll() {
        binding.apply {
            tvProgress.text = "0%"
            ivProgress.progress = 0.0F
        }

        nfcViewModel.startNfcAction(NfcAction.BiometricEnrollment)
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.card_error_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.try_again) { dialog, _ ->
                dialog.dismiss()
                startFingerprintEnroll()
            }
            .create()
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateBiometricProgress(progress: Int) {
        val calculatedProgress = progress * 0.218F / 100

        binding.apply {
            tvProgress.text = "$progress%"
            ivProgress.progress = calculatedProgress
        }
    }

    private fun navigateToBiometricDoneScreen() {
//        findNavController().navigate(
//            BiometricFingerEnrollFragmentDirections
//                .actionBiometricFingerEnrollFragmentToBiometricDoneFragment()
//        )
    }
}