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
}