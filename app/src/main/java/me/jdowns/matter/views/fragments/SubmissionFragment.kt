package me.jdowns.matter.views.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.adapters.SubmissionAdapter
import net.dean.jraw.models.Submission
import net.dean.jraw.pagination.Paginator

class SubmissionFragment : BaseFragmentWithRecyclerView<Submission>(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override val paginator: Paginator<Submission> = if (subreddit.isEmpty()) {
        Matter.accountHelper.reddit.frontPage().limit(25).build()
    } else {
        Matter.accountHelper.reddit.subreddit(subreddit).posts().limit(25).build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_submission, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<TextView>(R.id.action_bar_title)?.text =
                getString(if (subreddit.isEmpty()) R.string.front_page else R.string.subreddit_qualifier, subreddit)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_submission).apply {
            adapter = SubmissionAdapter(dataSet).apply {
                listener = this@SubmissionFragment
            }
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayout.VERTICAL
            }
        }
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout_view_submission).apply {
            setOnRefreshListener(this@SubmissionFragment)
            post {
                isRefreshing = true
                onRefresh()
            }
        }
    }

    override fun onRefresh() {
        if (dataSet.size > 0) {
            dataSet.clear()
            recyclerView.adapter.notifyDataSetChanged()
        }
        paginator.restart()
        tryGetMore()
    }

    override fun updateView() {
        stopLoading()
    }

    private fun stopLoading() {
        swipeRefreshLayout.isRefreshing = false
        with(recyclerView) {
            animate()
                .withStartAction { visibility = View.VISIBLE }
                .alpha(1.0F)
        }
    }

    companion object {
        const val FRAGMENT_TAG = "submissionFragmentTag"
        private lateinit var subreddit: String
        fun newInstance(subreddit: String?): SubmissionFragment {
            this.subreddit = subreddit ?: ""
            return SubmissionFragment()
        }
    }
}