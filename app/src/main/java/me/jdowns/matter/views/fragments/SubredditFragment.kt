package me.jdowns.matter.views.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.views.adapters.SubredditAdapter
import net.dean.jraw.models.Subreddit
import net.dean.jraw.pagination.Paginator

class SubredditFragment : BaseFragmentWithRecyclerView<Subreddit>(), SubredditAdapter.SubredditAdapterListener {
    override val paginator: Paginator<Subreddit> =
        Matter.accountHelper.reddit.me().subreddits("subscriber").limit(10).build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_subreddit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_subreddit_names).apply {
            adapter = SubredditAdapter(dataSet).apply {
                listener = this@SubredditFragment
            }
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }
        }
        tryGetMore()
    }

    override fun updateView() {
        addSubmissionFragment(dataSet[0].title)
    }

    override fun subredditClicked(subredditTitle: String) {
        addSubmissionFragment(subredditTitle)
    }

    private fun addSubmissionFragment(subredditName: String) {
        childFragmentManager.beginTransaction().replace(
            R.id.fragment_submission_container,
            SubmissionFragment.newInstance(subredditName),
            SubmissionFragment.FRAGMENT_TAG
        ).commit()
    }

    companion object {
        const val FRAGMENT_TAG = "subredditsFragmentTag"
    }
}