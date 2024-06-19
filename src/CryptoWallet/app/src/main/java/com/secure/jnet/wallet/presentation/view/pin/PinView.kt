//package com.secure.jnet.wallet.presentation.view.pin
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.view.animation.AnimationUtils
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.ViewPinBinding
//
//class PinView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs),
//    PinKeyboard.OnPinInsertListener {
//
//    private val binding =
//        ViewPinBinding.inflate(LayoutInflater.from(context), this, true)
//
//    private var pinListener: PinListener? = null
//
//    private val pinLimit = 6
//    private val pin = StringBuilder()
//    private val pinViews = mutableListOf<View>()
//
//    private var errorState = false
//
//    init {
//        init(attrs)
//    }
//
//    private fun init(attrs: AttributeSet?) {
//        pinViews.add(binding.pin1)
//        pinViews.add(binding.pin2)
//        pinViews.add(binding.pin3)
//        pinViews.add(binding.pin4)
//        pinViews.add(binding.pin5)
//        pinViews.add(binding.pin6)
//    }
//
//    fun setPinListener(pinListener: PinListener?) {
//        this.pinListener = pinListener
//    }
//
//    fun setupWithKeyboard(pinKeyboard: PinKeyboard) {
//        pinKeyboard.setOnPinInsertListener(this)
//    }
//
//    override fun onPinInserted(pin: String) {
//        if (errorState) {
//            errorState = false
//            showErrorPinView(false)
//        }
//
//        if (pin.isEmpty()) {
//            onDeleteClick()
//        } else if (Character.isDigit(pin[0])) {
//            onPinClick(pin.substring(0, 1).toInt())
//        }
//    }
//
//    private fun onDeleteClick() {
//        if (pin.isNotEmpty()) {
//            pin.deleteCharAt(pin.length - 1)
//        }
//        handlePinInsert()
//    }
//
//    private fun onPinClick(digit: Int) {
//        if (pin.length < pinLimit) {
//            pin.append(digit)
//        }
//        handlePinInsert()
//    }
//
//    private fun handlePinInsert() {
//        updatePinView(pin.length)
//        if (pin.length == pinLimit) {
//            pinListener?.onPinEntered(pin.toString())
//        }
//    }
//
//    private fun updatePinView(pinLength: Int) {
//        for (i in pinViews.indices) {
//            pinViews[i].isSelected = i <= pinLength - 1
//        }
//    }
//
//    private fun showErrorPinView(error: Boolean) {
//        if (error) {
//            for (i in pinViews.indices) {
//                pinViews[i].isSelected = false
//                pinViews[i].background = ContextCompat.getDrawable(context, R.drawable.ic_pin_dot_error)
//            }
//            binding.tvPinError.visibility = View.VISIBLE
//            binding.llPinContainer.startAnimation(AnimationUtils.loadAnimation(context,R.anim.pin_error))
//        } else {
//            for (i in pinViews.indices) {
//                pinViews[i].background = ContextCompat.getDrawable(context, R.drawable.bg_pin_dot)
//            }
//            binding.tvPinError.visibility = View.INVISIBLE
//        }
//    }
//
//    fun cleanPinView() {
//        pin.clear()
//        updatePinView(0)
//    }
//
//    fun showError() {
//        pin.clear()
//        errorState = true
//        showErrorPinView(true)
//    }
//
//    interface PinListener {
//        fun onPinEntered(pin: String)
//    }
//
//}