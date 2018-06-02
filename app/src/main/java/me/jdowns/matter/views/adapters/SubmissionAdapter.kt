package me.jdowns.matter.views.adapters

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import net.dean.jraw.models.Submission
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class SubmissionAdapter(private val dataSet: List<Submission>) : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView = view.findViewById<TextView>(R.id.submission_username)
        val postTimeTextView = view.findViewById<TextView>(R.id.submission_post_time)
        val typeTextView = view.findViewById<TextView>(R.id.submission_type)
        val titleTextView = view.findViewById<TextView>(R.id.submission_title)
        val tagTextView = view.findViewById<TextView>(R.id.submission_tag)
        val imageImageButton = view.findViewById<ImageButton>(R.id.submission_image)
        val upvoteImageButton = view.findViewById<ImageButton>(R.id.submission_upvote)
        val voteCountTextView = view.findViewById<TextView>(R.id.submission_vote_count)
        val downvoteImageButton = view.findViewById<ImageButton>(R.id.submission_downvote)
        val commentCountTextView = view.findViewById<TextView>(R.id.submission_comment_count)

        init {
            if (!Matter.accountHelper.isAuthenticated()) {
                upvoteImageButton.visibility = View.GONE
                downvoteImageButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_submission, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val submission = dataSet[position]
        holder.usernameTextView.text = submission.author
        with(getTimeDiff(submission.created)) {
            holder.postTimeTextView.text = first.toString().plus(second)
        }
        holder.typeTextView.text = submission.postHint
        holder.titleTextView.text = submission.title
        if (submission.linkFlairText.isNullOrBlank()) {
            holder.tagTextView.visibility = View.GONE
        } else {
            holder.tagTextView.text = submission.linkFlairText
        }
        if (submission.hasThumbnail() && !submission.thumbnail.isNullOrBlank()) {
            async(UI) {
                holder.imageImageButton.setImageDrawable(
                    async {
                        Drawable.createFromStream(
                            URL(submission.thumbnail).content as InputStream,
                            "submissionImage"
                        )
                    }.await()
                )
            }
        } else {
            holder.imageImageButton.visibility = View.GONE
        }
        if (submission.isScoreHidden) {
            holder.voteCountTextView.text = "?"
        } else {
            holder.voteCountTextView.text = submission.score.toString()
        }
        holder.commentCountTextView.text = submission.commentCount.toString()
    }

    override fun getItemCount(): Int = dataSet.size

    private fun getTimeDiff(submissionDate: Date): Pair<Long, String> {
        with((Date().time - submissionDate.time) / 1000L) {
            return when {
                this < 60 -> Pair(TimeUnit.SECONDS.convert(this, TimeUnit.SECONDS), "s")
                this < 3600 -> Pair(TimeUnit.MINUTES.convert(this, TimeUnit.SECONDS), "m")
                this < 86400 -> Pair(TimeUnit.HOURS.convert(this, TimeUnit.SECONDS), "h")
                this < 31536000 -> Pair(TimeUnit.DAYS.convert(this, TimeUnit.SECONDS), "d")
                else -> Pair(TimeUnit.DAYS.convert(this, TimeUnit.SECONDS) / 365, "y")
            }
        }
    }
}