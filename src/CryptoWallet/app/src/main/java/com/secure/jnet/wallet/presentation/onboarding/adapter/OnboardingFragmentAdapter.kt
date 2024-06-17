//package com.secure.jnet.wallet.presentation.onboarding.adapter
//
//import androidx.fragment.app.Fragment
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.presentation.onboarding.OnboardingLayoutFragment
//
//class OnboardingFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
//
//    override fun getItemCount(): Int = 3
//
//    override fun createFragment(position: Int): Fragment {
//        return when(position) {
//            0 -> {
//                OnboardingLayoutFragment.newInstance(
//                    R.drawable.img_onboarding_1,
//                    R.string.onboarding_1_title,
//                    R.string.onboarding_1_subtitle,
//                )
//            }
//            1 -> {
//                OnboardingLayoutFragment.newInstance(
//                    R.drawable.img_onboarding_2,
//                    R.string.onboarding_2_title,
//                    R.string.onboarding_2_subtitle,
//                )
//            }
//            2 -> {
//                OnboardingLayoutFragment.newInstance(
//                    R.drawable.img_onboarding_3,
//                    R.string.onboarding_3_title,
//                    R.string.onboarding_3_subtitle,
//                )
//            }
//            else -> {
//                throw IllegalStateException("Wrong onboarding screen number")
//            }
//        }
//    }
//
//
//}