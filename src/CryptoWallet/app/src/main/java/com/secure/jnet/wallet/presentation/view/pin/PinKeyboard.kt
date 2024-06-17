package com.secure.jnet.wallet.presentation.view.pin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.secure.jnet.wallet.databinding.ViewPinKeyboardBinding

class PinKeyboard(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs),
    View.OnClickListener {

    private val binding =
        ViewPinKeyboardBinding.inflate(LayoutInflater.from(context), this, true)

    private var onPinInsertListener: OnPinInsertListener? = null
    private var onBiometricClickListener: OnBiometricClickListener? = null

    private val pinButtons = mutableListOf<TextView>()

    init {
        setWillNotDraw(false)

        pinButtons.add(binding.num0)
        pinButtons.add(binding.num1)
        pinButtons.add(binding.num2)
        pinButtons.add(binding.num3)
        pinButtons.add(binding.num4)
        pinButtons.add(binding.num5)
        pinButtons.add(binding.num6)
        pinButtons.add(binding.num7)
        pinButtons.add(binding.num8)
        pinButtons.add(binding.num9)

        pinButtons.forEach {
            it.setOnClickListener(this)
        }

        binding.delete.setOnClickListener {
            onPinInsertListener?.onPinInserted("")
        }

        binding.biometric.setOnClickListener {
            onBiometricClickListener?.onBiometricClicked()
        }

    }

    override fun onClick(v: View?) {
        onPinInsertListener?.onPinInserted((v as? TextView)?.text.toString())
    }

    fun setOnPinInsertListener(onPinInsertListener: OnPinInsertListener?) {
        this.onPinInsertListener = onPinInsertListener
    }

    fun setOnBiometricClickListener(onBiometricClickListener: OnBiometricClickListener?) {
        this.onBiometricClickListener = onBiometricClickListener
    }

    fun setBiometricEnabled(enabled: Boolean) {
        binding.biometric.visibility = if (enabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    fun randomizeKeyboard() {
        val pinNumbers = (0..9).shuffled().toSet()
        pinButtons.forEachIndexed { index, textView ->
            textView.text = pinNumbers.elementAt(index).toString()
        }
    }

    interface OnPinInsertListener {
        fun onPinInserted(pin: String)
    }

    interface OnBiometricClickListener {
        fun onBiometricClicked()
    }

}