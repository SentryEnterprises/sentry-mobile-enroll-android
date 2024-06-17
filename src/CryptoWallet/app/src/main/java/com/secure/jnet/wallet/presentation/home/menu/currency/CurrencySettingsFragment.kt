//package com.secure.jnet.wallet.presentation.home.menu.currency
//
//import android.os.Bundle
//import android.view.View
//import android.widget.RadioButton
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentCurrencySettingsBinding
//import com.secure.jnet.wallet.domain.interactor.PreferencesInteractor
//import com.secure.jnet.wallet.domain.models.enums.PrimaryCurrency
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class CurrencySettingsFragment :
//    BaseFragment<FragmentCurrencySettingsBinding>(R.layout.fragment_currency_settings) {
//
//    @Inject
//    lateinit var preferencesInteractor: PreferencesInteractor
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        when(preferencesInteractor.primaryCurrency) {
//            PrimaryCurrency.FIAT -> binding.rbFiat.isChecked = true
//            PrimaryCurrency.CRYPTO -> binding.rbCrypto.isChecked = true
//        }
//
//        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
//
//        binding.rbCrypto.setOnClickListener(this::onRadioButtonClicked)
//        binding.rbFiat.setOnClickListener(this::onRadioButtonClicked)
//    }
//
//    private fun onRadioButtonClicked(view: View) {
//        if (view is RadioButton) {
//            val checked = view.isChecked
//            when (view.getId()) {
//                R.id.rbFiat ->
//                    if (checked) {
//                        preferencesInteractor.primaryCurrency = PrimaryCurrency.FIAT
//                    }
//                R.id.rbCrypto ->
//                    if (checked) {
//                        preferencesInteractor.primaryCurrency = PrimaryCurrency.CRYPTO
//                    }
//            }
//        }
//    }
//}