package me.jdowns.matter.views.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView

abstract class BaseRecyclerView(context: Context) : RecyclerView(context) {
    abstract class BaseAdapter<ViewHolder : RecyclerView.ViewHolder?, T>(private val dataSet: List<T>) :
        Adapter<ViewHolder>() {
        lateinit var listener: BaseAdapterListener
        override fun getItemCount(): Int = dataSet.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            /** TODO: Investigate loading before the last element is in view */
            if (itemCount > 0 && position == itemCount - 1) {
                listener.atEnd()
            }
        }
    }

    interface BaseAdapterListener {
        fun atEnd()
    }

    abstract class BaseVerticalScrollListener : OnScrollListener() {
        var distance = 0
        private val minimum = 3
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            when {
                distance > minimum -> {
                    scrollingDown()
                    distance = 0
                }
                distance < -minimum -> {
                    scrollingUp()
                    distance = 0
                }
                else -> distance += dy
            }
        }

        abstract fun scrollingDown()
        abstract fun scrollingUp()
    }
}