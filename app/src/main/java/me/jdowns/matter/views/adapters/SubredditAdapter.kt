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
            setSubredditView(holder, this)
        }

        super.onBindViewHolder(holder, position)
    }

    private fun setSubredditView(holder: ViewHolder, subreddit: Subreddit) {
        with(holder.subredditTitle) {
            text = subreddit.name.toUpperCase()
            setOnClickListener({
                (listener as SubredditAdapterListener).subredditClicked(subreddit.name)
            })
        }
    }

    interface SubredditAdapterListener : BaseRecyclerView.BaseAdapterListener {
        fun subredditClicked(subredditTitle: String)
    }
}