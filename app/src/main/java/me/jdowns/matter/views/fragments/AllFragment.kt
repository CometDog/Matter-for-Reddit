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

class AllFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val dataSet: MutableList<Submission> = mutableListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_all).apply {
            adapter = SubmissionAdapter(dataSet)
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayout.VERTICAL
            }
        }

        /** TODO: Implement full OAuth */
        // This is used for general testing
        async {
            dataSet.addAll(Matter.accountHelper.switchToUserless().subreddit("all").posts().limit(25).build().next())
            launch(UI) {
                stopInitialLoading()
                recyclerView.adapter.notifyItemRangeInserted(recyclerView.adapter.itemCount, dataSet.size - 1)
            }
        }
    }

    private fun stopInitialLoading() {
        stopLoading()
        with(recyclerView) {
            animate()
                .withStartAction { visibility = View.VISIBLE }
                .alpha(1.0F)
        }
    }

    private fun stopLoading() {
        with(view!!.findViewById<ProgressBar>(R.id.fragment_all_progress_bar)) {
            animate()
                .alpha(0.0f)
                .withEndAction { visibility = View.GONE }
        }
    }

    companion object {
        const val FRAGMENT_TAG = "allFragmentTag"
    }
}