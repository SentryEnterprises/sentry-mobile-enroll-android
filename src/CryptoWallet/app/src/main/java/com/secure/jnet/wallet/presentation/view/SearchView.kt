package com.secure.jnet.wallet.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.secure.jnet.wallet.databinding.ViewSearchBinding

class SearchView(
    context: Context,
    attrs: AttributeSet?,
) : LinearLayout(context, attrs) {

    private val binding =
        ViewSearchBinding.inflate(LayoutInflater.from(context), this, true)

    private var searchTextListener: OnSearchTextListener? = null

    init {
        binding.apply {
            etSearch.apply {
                doAfterTextChanged {
                    ivClose.isVisible = (it?.toString()?.length ?: 0) > 0

                    searchTextListener?.onSearchTextChanged(it.toString())
                }

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchTextListener?.onSearchTextChanged(etSearch.text.toString())
                        true
                    } else {
                        false
                    }
                }
            }

            ivClose.setOnClickListener {
                etSearch.text.clear()

                searchTextListener?.onSearchTextChanged("")
            }
        }
    }

    fun setSearchTextChangeListener(searchTextListener: OnSearchTextListener) {
        this.searchTextListener = searchTextListener
    }

    interface OnSearchTextListener {
        fun onSearchTextChanged(text: String)
    }
}