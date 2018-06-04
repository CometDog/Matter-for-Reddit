package me.jdowns.matter.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.jdowns.matter.R
import me.jdowns.matter.views.widgets.BaseRecyclerView
import net.dean.jraw.models.Subreddit

class SubredditAdapter(private val dataSet: List<Subreddit>) :
    BaseRecyclerView.BaseAdapter<SubredditAdapter.ViewHolder, Subreddit>(dataSet) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subredditTitle = view.findViewById<TextView>(R.id.subreddit_title)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.view_subreddit_name,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(dataSet[position]) {
            setSubredditTitle(holder, this)
            setSubredditClickListener(holder, this)
        }

        super.onBindViewHolder(holder, position)
    }

    private fun setSubredditTitle(holder: ViewHolder, subreddit: Subreddit) {
        holder.subredditTitle.text =
                holder.subredditTitle.resources.getString(R.string.subreddit_qualifier, subreddit.name)
    }

    private fun setSubredditClickListener(holder: ViewHolder, subreddit: Subreddit) {
        holder.subredditTitle.setOnClickListener({
            (listener as SubredditAdapterListener).subredditClicked(subreddit.name)
        })
    }

    interface SubredditAdapterListener : BaseRecyclerView.BaseAdapterListener {
        fun subredditClicked(subredditTitle: String)
    }
}