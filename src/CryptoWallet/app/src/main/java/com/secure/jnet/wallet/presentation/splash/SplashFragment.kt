package com.secure.jnet.wallet.presentation.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.databinding.FragmentSplashBinding
import com.secure.jnet.wallet.presentation.base.BaseFragment
import com.secure.jnet.wallet.util.START_DELAY
import com.secure.jnet.wallet.util.ext.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(
    R.layout.fragment_splash
) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivLogo.isVisible = true
            ivTitle.isVisible = true
        }
    }

    override fun onBindLiveData() {
        observe(viewModel.navigateToAttachCardScreen) {
            navigateToAttachCardScreen()
        }

//        observe(viewModel.navigateToOnboardingScreen) {
//            navigateToOnboardingScreen()
//        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

//    private fun navigateToOnboardingScreen() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            view?.post {
//                findNavController().navigate(
//                    SplashFragmentDirections.actionSplashFragmentToOnboardingFragment()
//                )
//            }
//        }, START_DELAY)
//    }

    private fun navigateToAttachCardScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            view?.post {
                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToAttachCardFragment()
                )
            }
        }, START_DELAY)
    }
}