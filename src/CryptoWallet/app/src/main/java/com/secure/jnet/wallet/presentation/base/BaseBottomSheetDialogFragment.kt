//package com.secure.jnet.wallet.presentation.base
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.LayoutRes
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//
//abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding>
//    (@LayoutRes private val layoutResId: Int) : BottomSheetDialogFragment() {
//
//    protected lateinit var binding: T
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
//        binding.lifecycleOwner = this
//        return binding.root
//    }
//
//}