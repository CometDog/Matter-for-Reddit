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
import me.jdowns.matter.views.widgets.ScrollableNavigationView
import net.dean.jraw.models.Subreddit
import net.dean.jraw.pagination.Paginator

class SubredditFragment : BaseFragmentWithRecyclerView<Subreddit>(), SubredditAdapter.SubredditAdapterListener,
    SubmissionFragment.SubmissionFragmentListener {
    override val paginator: Paginator<Subreddit> =
        Matter.accountHelper.reddit.me().subreddits("subscriber").limit(10).build()
    private lateinit var navigationView: ScrollableNavigationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_subreddit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationView = view.findViewById(R.id.subreddits_navigation_bar)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_subreddit_names).also {
            it.adapter = SubredditAdapter(dataSet).also {
                it.listener = this
            }
            it.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }
        }
        tryGetMore()
    }

    override fun updateView() = addSubmissionFragment(dataSet[0].title)

    override fun subredditClicked(subredditTitle: String) = addSubmissionFragment(subredditTitle)

    override fun scrollingDown() = navigationView.tryStartHideAnimation()

    override fun scrollingUp() = navigationView.tryStartShowAnimation()

    private fun addSubmissionFragment(subredditName: String) {
        childFragmentManager.beginTransaction().replace(
            R.id.fragment_submission_container,
            SubmissionFragment.newInstance(subredditName).also {
                it.listener = this
            },
            SubmissionFragment.FRAGMENT_TAG
        ).commit()
    }

    companion object {
        const val FRAGMENT_TAG = "subredditsFragmentTag"
    }
}