//package com.secure.jnet.wallet.presentation.onboarding
//
//import android.os.Bundle
//import android.view.View
//import androidx.annotation.DrawableRes
//import androidx.annotation.StringRes
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.databinding.FragmentOnboardingLayoutBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class OnboardingLayoutFragment :
//    BaseFragment<FragmentOnboardingLayoutBinding>(R.layout.fragment_onboarding_layout) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        arguments?.takeIf { it.containsKey(ARG_IMG_ID) }?.apply {
//            binding.ivPicture.setImageResource(getInt(ARG_IMG_ID))
//        }
//
//        arguments?.takeIf { it.containsKey(ARG_TITLE_ID) }?.apply {
//            binding.tvTitle.setText(getInt(ARG_TITLE_ID))
//        }
//
//        arguments?.takeIf { it.containsKey(ARG_SUBTITLE_ID) }?.apply {
//            binding.tvSubtitle.setText(getInt(ARG_SUBTITLE_ID))
//        }
//    }
//
//    companion object {
//        private const val ARG_IMG_ID = "imgId"
//        private const val ARG_TITLE_ID = "titleId"
//        private const val ARG_SUBTITLE_ID = "subtitleId"
//
//        fun newInstance(
//            @DrawableRes imgId: Int,
//            @StringRes titleId: Int,
//            @StringRes subtitleId: Int,
//        ) = OnboardingLayoutFragment().apply {
//            arguments = Bundle().apply {
//                putInt(ARG_IMG_ID, imgId)
//                putInt(ARG_TITLE_ID, titleId)
//                putInt(ARG_SUBTITLE_ID, subtitleId)
//            }
//        }
//    }
//}