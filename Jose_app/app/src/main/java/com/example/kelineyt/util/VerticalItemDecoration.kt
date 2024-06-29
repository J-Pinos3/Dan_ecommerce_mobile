package com.example.kelineyt.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hilt_aggregated_deps._com_example_kelineyt_fragments_shopping_ProductsDetailFragment_GeneratedInjector

class VerticalItemDecoration(private val amount: Int = 0): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    }

}