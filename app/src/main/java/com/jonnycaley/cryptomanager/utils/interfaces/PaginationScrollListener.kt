package com.jonnycaley.cryptomanager.utils.interfaces

import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

abstract class PaginationScrollListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
(var layoutManager: LinearLayoutManager) : NestedScrollView.OnScrollChangeListener {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {

        if (scrollY == (  v!!.getChildAt(0).measuredHeight - v.measuredHeight)) {
            loadMoreItems()
        }
    }

    val TAG = "PaginationScroll"

    abstract fun loadMoreItems()
}