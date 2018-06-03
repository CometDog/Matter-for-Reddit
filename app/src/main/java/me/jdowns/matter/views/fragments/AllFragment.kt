package me.jdowns.matter.views.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.adapters.SubmissionAdapter
import net.dean.jraw.models.Submission
import net.dean.jraw.pagination.DefaultPaginator

class AllFragment : Fragment() {
    private lateinit var paginator: DefaultPaginator<Submission>
    private lateinit var recyclerView: RecyclerView
    private val dataSet: MutableList<Submission> = mutableListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_all).apply {
            adapter = SubmissionAdapter(dataSet).apply {
                listener = object : SubmissionAdapter.SubmissionRecyclerViewListener {
                    override fun atBottom() {
                        getNextPage()
                    }
                }
            }
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayout.VERTICAL
            }
        }
        getNextPage(true)
    }

    private fun getNextPage(firstPage: Boolean = false) {
        async {
            if (!::paginator.isInitialized) {
                /** TODO: Implement full OAuth */
                // This is used for general testing
                paginator = Matter.accountHelper.switchToUserless().subreddit("all").posts().limit(25).build()
            }
            val newDataSet = paginator.next()
            dataSet.addAll(newDataSet)
            launch(UI) {
                if (firstPage) {
                    stopInitialLoading()
                }
                recyclerView.adapter.notifyItemRangeInserted(recyclerView.adapter.itemCount, newDataSet.size - 1)
            }
        }

    }

    private fun stopInitialLoading() {
        with(view!!.findViewById<ProgressBar>(R.id.fragment_all_initial_progress_bar)) {
            animate()
                .alpha(0.0f)
                .withEndAction { visibility = View.GONE }
        }
        with(recyclerView) {
            animate()
                .withStartAction { visibility = View.VISIBLE }
                .alpha(1.0F)
        }
    }

    companion object {
        const val FRAGMENT_TAG = "allFragmentTag"
    }
}