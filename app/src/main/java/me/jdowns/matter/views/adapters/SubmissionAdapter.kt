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
import me.jdowns.matter.R
import net.dean.jraw.models.Submission
import java.io.InputStream
import java.net.URL

class SubmissionAdapter(private val dataSet: List<Submission>) : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView = view.findViewById<TextView>(R.id.submission_username)
        val postTimeTextView = view.findViewById<TextView>(R.id.submission_post_time)
        val typeTextView = view.findViewById<TextView>(R.id.submission_type)
        val titleTextView = view.findViewById<TextView>(R.id.submission_title)
        val tagTextView = view.findViewById<TextView>(R.id.submission_tag)
        val imageImageButton = view.findViewById<ImageButton>(R.id.submission_image)
        val voteCountTextView = view.findViewById<TextView>(R.id.submission_vote_count)
        val commentCountTextView = view.findViewById<TextView>(R.id.submission_comment_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_submission, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val submission = dataSet[position]
        holder.usernameTextView.text = submission.author
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
}