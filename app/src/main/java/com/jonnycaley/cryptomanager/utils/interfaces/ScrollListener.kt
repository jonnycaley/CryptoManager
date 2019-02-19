package com.jonnycaley.cryptomanager.utils.interfaces

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class ScrollListener : ScrollView {
    private var scrollViewListener: PaginationScrollListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setScrollViewListener(scrollViewListener: PaginationScrollListener) {
        this.scrollViewListener = scrollViewListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (scrollViewListener != null) {
            scrollViewListener!!.onScrollChanged(this, l, t, oldl, oldt)
        }
    }
}