//package com.secure.jnet.wallet.presentation.common.adapter
//
//import android.graphics.Canvas
//import android.graphics.drawable.Drawable
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//
//class TransactionItemDecoration(private val divider: Drawable, private val viewType: Int) :
//    RecyclerView.ItemDecoration() {
//
//    override fun onDrawOver(
//        canvas: Canvas,
//        parent: RecyclerView,
//        state: RecyclerView.State,
//    ) {
//        val childCount = parent.childCount
//
//        val left = parent.paddingLeft
//        val right = parent.width - parent.paddingRight
//
//        for (i in 0 until childCount) {
//            val child = parent.getChildAt(i)
//            val params = child.layoutParams as ViewGroup.MarginLayoutParams
//
//            val itemPosition = parent.getChildAdapterPosition(child)
//            val totalItemCount = parent.adapter?.itemCount ?: 0
//            val nextItemPosition = itemPosition + 1
//
//            val isLastPosition = itemPosition == totalItemCount - 1
//
//            if (parent.adapter?.getItemViewType(itemPosition) == viewType
//                && !isLastPosition
//                && parent.adapter?.getItemViewType(nextItemPosition) == viewType
//            ) {
//                val top = child.bottom + params.bottomMargin
//                val bottom = top + divider.intrinsicHeight
//
//                divider.setBounds(left, top, right, bottom)
//                divider.draw(canvas)
//            }
//        }
//    }
//}