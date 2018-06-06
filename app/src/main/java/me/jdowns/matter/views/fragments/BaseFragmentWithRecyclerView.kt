package me.jdowns.matter.views.fragments

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.views.widgets.BaseRecyclerView
import net.dean.jraw.models.Listing
import net.dean.jraw.models.UniquelyIdentifiable
import net.dean.jraw.pagination.Paginator

abstract class BaseFragmentWithRecyclerView<T : UniquelyIdentifiable> : android.support.v4.app.Fragment(),
    BaseRecyclerView.BaseAdapterListener {
    protected open val paginator: Paginator<T>? = null
    protected lateinit var recyclerView: RecyclerView
    protected val dataSet: MutableList<T> = mutableListOf()
    protected lateinit var job: Deferred<Listing<T>>

    override fun atEnd() = tryGetMore()

    protected abstract fun updateView()

    protected fun tryGetMore() {
        if (paginator!!.iterator().hasNext()) {
            launch(UI) {
                getMore()
            }
        } else {
            logNoMorePage()
        }
    }

    @UiThread
    private suspend fun getMore() {
        val newDataSet = async {
            paginator!!.next()
        }.await()
        dataSet.addAll(newDataSet)
        recyclerView.adapter!!.notifyItemRangeInserted(
            (recyclerView.adapter!!.itemCount + if (recyclerView.adapter!!.itemCount == newDataSet.size) 0 else 1) - newDataSet.size,
            newDataSet.size
        )
        updateView()
    }

    private fun logNoMorePage() {
        Log.i("outOfPages", "Reached the end of paginator")
    }
}