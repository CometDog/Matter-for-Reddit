package me.jdowns.matter.views.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

        async {
            dataSet.addAll(Matter.accountHelper.switchToUserless().subreddit("all").posts().limit(10).build().next())
            launch(UI) {
                recyclerView.adapter.notifyItemRangeInserted(recyclerView.adapter.itemCount, dataSet.size - 1)
            }
        }
    }

    companion object {
        const val FRAGMENT_TAG = "allFragmentTag"
    }
}