//package com.secure.jnet.wallet.presentation.home.menu.autolock
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentAutoLockSettingsBinding
//import com.secure.jnet.wallet.domain.interactor.PreferencesInteractor
//import com.secure.jnet.wallet.domain.models.enums.AutoLockTime
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class AutoLockSettingsFragment :
//    BaseFragment<FragmentAutoLockSettingsBinding>(R.layout.fragment_auto_lock_settings) {
//
//    @Inject
//    lateinit var preferencesInteractor: PreferencesInteractor
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
//
//        binding.btn15Sec.setOnClickListener { onTimeButtonClick(AutoLockTime.SEC_15.time) }
//        binding.btn30Sec.setOnClickListener { onTimeButtonClick(AutoLockTime.SEC_30.time) }
//        binding.btn60Sec.setOnClickListener { onTimeButtonClick(AutoLockTime.SEC_60.time) }
//        binding.btn5min.setOnClickListener { onTimeButtonClick(AutoLockTime.SEC_300.time) }
//
//        selectAppropriateTimeView(preferencesInteractor.autoLockTime)
//    }
//
//    private fun onTimeButtonClick(time: Long) {
//        selectAppropriateTimeView(time)
//        preferencesInteractor.autoLockTime = time
//    }
//
//    private fun selectAppropriateTimeView(time: Long) {
//        binding.viewChecked15Sec.isVisible = false
//        binding.viewChecked30Sec.isVisible = false
//        binding.viewChecked60Sec.isVisible = false
//        binding.viewChecked5min.isVisible = false
//
//        when (time) {
//            AutoLockTime.SEC_15.time -> binding.viewChecked15Sec.isVisible = true
//            AutoLockTime.SEC_30.time -> binding.viewChecked30Sec.isVisible = true
//            AutoLockTime.SEC_60.time -> binding.viewChecked60Sec.isVisible = true
//            AutoLockTime.SEC_300.time -> binding.viewChecked5min.isVisible = true
//        }
//    }
//}