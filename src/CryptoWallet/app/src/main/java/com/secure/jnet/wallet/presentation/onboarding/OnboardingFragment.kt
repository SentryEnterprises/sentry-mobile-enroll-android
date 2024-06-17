//package com.secure.jnet.wallet.presentation.onboarding
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.google.android.material.tabs.TabLayoutMediator
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentOnboardingBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.onboarding.adapter.OnboardingFragmentAdapter
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>(
//    R.layout.fragment_onboarding
//) {
//
//    private val viewModel: OnboardingViewModel by viewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            viewPager.adapter = OnboardingFragmentAdapter(this@OnboardingFragment)
//
//            TabLayoutMediator(tabLayout,  viewPager) { _, _ -> }.attach()
//
//            btnStart.setOnClickListener {
//                viewModel.onStartClick()
//            }
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.navigateToAttachCardScreen) {
//            navigateToAttachCardScreen()
//        }
//    }
//
//    private fun navigateToAttachCardScreen() {
//        findNavController().navigate(
//            OnboardingFragmentDirections.actionOnboardingMainFragmentToAttachCardFragment()
//        )
//    }
//}