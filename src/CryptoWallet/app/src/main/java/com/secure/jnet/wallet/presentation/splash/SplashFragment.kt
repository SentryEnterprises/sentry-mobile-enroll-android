package com.secure.jnet.wallet.presentation.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.databinding.FragmentSplashBinding
import androidx.fragment.app.Fragment
import com.secure.jnet.wallet.util.START_DELAY
import com.secure.jnet.wallet.util.ext.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment(
    R.layout.fragment_splash
) {

    private lateinit var binding: FragmentSplashBinding

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivLogo.isVisible = true
            ivTitle.isVisible = true
        }

        observe(viewModel.navigateToAttachCardScreen) {
            navigateToAttachCardScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

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