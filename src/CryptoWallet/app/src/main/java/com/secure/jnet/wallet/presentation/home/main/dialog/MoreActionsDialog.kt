//package com.secure.jnet.wallet.presentation.home.main.dialog
//
//import android.app.Dialog
//import android.os.Bundle
//import android.view.View
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.presentation.base.BaseBottomSheetDialogFragment
//import com.secure.jnet.wallet.databinding.DialogMoreActionsBinding
//
//class MoreActionsDialog :
//    BaseBottomSheetDialogFragment<DialogMoreActionsBinding>(R.layout.dialog_more_actions) {
//
//    enum class Action {
//        ACTION_BUY,
//        ACTION_CASH_OUT,
//        ACTION_SWAP,
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialog)
//        return super.onCreateDialog(savedInstanceState)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.btnBuy.setOnClickListener { returnResult(Action.ACTION_BUY) }
//        binding.btnCashOut.setOnClickListener { returnResult(Action.ACTION_CASH_OUT) }
//        binding.btnSwap.setOnClickListener { returnResult(Action.ACTION_SWAP) }
//    }
//
//    private fun returnResult(action: Action) {
//        val bundle = Bundle().apply {
//            putString(ACTION_EXTRA, action.name)
//        }
//        parentFragmentManager.setFragmentResult(ACTION_KEY, bundle)
//        dismissAllowingStateLoss()
//    }
//
//    companion object {
//        const val TAG = "MORE_ACTIONS_DIALOG"
//        const val ACTION_KEY = "actionKey"
//        const val ACTION_EXTRA = "actionExtra"
//
//        fun newInstance() = MoreActionsDialog()
//    }
//}