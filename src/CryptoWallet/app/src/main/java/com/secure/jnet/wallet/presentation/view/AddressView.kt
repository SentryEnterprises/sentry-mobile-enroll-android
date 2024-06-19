//package com.secure.jnet.wallet.presentation.view
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.inputmethod.EditorInfo
//import android.widget.LinearLayout
//import androidx.core.view.isVisible
//import androidx.core.widget.doAfterTextChanged
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.ViewAddressBinding
//
//class AddressView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
//
//    private val binding =
//        ViewAddressBinding.inflate(LayoutInflater.from(context), this, true)
//
//    private var onAddressViewClickListener: OnAddressViewClickListener? = null
//    private var onAddressChangeListener: OnAddressChangeListener? = null
//
//    init {
//        setOnEditorActionListener()
//
//        binding.apply {
//            btnScanQR.setOnClickListener { onAddressViewClickListener?.onScanQRClicked() }
//            btnPaste.setOnClickListener { onAddressViewClickListener?.onPasteClicked() }
//
//            apply {
//                btnClear.setOnClickListener {
//                    etAddress.setText("")
//                    onAddressViewClickListener?.onClearClicked()
//                }
//
//                etAddress.doAfterTextChanged {
//                    if (it.toString().isBlank()) {
//                        btnPaste.isVisible = true
//                        btnScanQR.isVisible = true
//
//                        btnClear.isVisible = false
//                        ivAddressStatus.isVisible = false
//                    } else {
//                        btnPaste.isVisible = false
//                        btnScanQR.isVisible = false
//
//                        btnClear.isVisible = true
//                        ivAddressStatus.isVisible = true
//                    }
//                }
//            }
//        }
//    }
//
//    private fun setOnEditorActionListener() {
//        binding.etAddress.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                onAddressChangeListener?.onAddressChanged(binding.etAddress.text.toString())
//                true
//            } else {
//                false
//            }
//        }
//    }
//
//    fun setOnClickListener(onAddressViewClickListener: OnAddressViewClickListener) {
//        this.onAddressViewClickListener = onAddressViewClickListener
//    }
//
//    fun setAddress(address: String) {
//        binding.apply {
//            etAddress.setText(address)
//            etAddress.setSelection(binding.etAddress.length())
//        }
//    }
//
//    fun setAddressValid(valid: Boolean) {
//        binding.apply {
//            if (valid) {
//                ivAddressStatus.setImageResource(R.drawable.ic_address_valid)
//            } else {
//                ivAddressStatus.setImageResource(R.drawable.ic_address_invalid)
//            }
//        }
//    }
//
//    interface OnAddressViewClickListener {
//        fun onPasteClicked()
//        fun onScanQRClicked()
//        fun onClearClicked()
//    }
//
//    interface OnAddressChangeListener {
//        fun onAddressChanged(address: String)
//    }
//}