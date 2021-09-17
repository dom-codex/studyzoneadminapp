package com.sparktech.studyzoneadmin.helpers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RcvScrollHandler(
    private val rcv: RecyclerView,
    private val getLoadingState: () -> Boolean,
    private val getListSize: () -> Int,
    private val setLoaderItem: () -> Unit,
    private val fetchMoreData: () -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val isLoading = getLoadingState()
        if (!isLoading) {
            val lm = rcv.layoutManager as LinearLayoutManager
            val listSize = getListSize()
            if (lm.findLastCompletelyVisibleItemPosition() == listSize - 1) {
                setLoaderItem()
                //rcv.adapter?.notifyItemInserted(listSize - 1)
                fetchMoreData()
            }
        }
        super.onScrollStateChanged(recyclerView, newState)
    }
}