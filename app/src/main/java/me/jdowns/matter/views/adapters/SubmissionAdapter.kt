package me.jdowns.matter.views.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.jdowns.matter.Matter
import me.jdowns.matter.R
import me.jdowns.matter.helpers.BitmapLruCache
import me.jdowns.matter.helpers.FlowableLeadingMarginSpan2
import net.dean.jraw.models.Submission
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class SubmissionAdapter(private val dataSet: List<Submission>) : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {
    lateinit var listener: SubmissionRecyclerViewListener
    private val thumbnailCache = BitmapLruCache()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val submissionCardViewLayout = view.findViewById<ViewGroup>(R.id.submission_card_view_layout)!!
        val usernameTextView = view.findViewById<TextView>(R.id.submission_username)!!
        val postTimeTextView = view.findViewById<TextView>(R.id.submission_post_time)!!
        val typeTextView = view.findViewById<TextView>(R.id.submission_type)!!
        val typeSeparator = view.findViewById<ViewGroup>(R.id.submission_type_separator)!!
        val titleTextView = view.findViewById<TextView>(R.id.submission_title)!!
        val subredditTextView = view.findViewById<TextView>(R.id.submission_subreddit)!!
        val subredditSeparator = view.findViewById<ViewGroup>(R.id.submission_subreddit_separator)!!
        val tagTextView = view.findViewById<TextView>(R.id.submission_tag)!!
        val imageCardView = view.findViewById<CardView>(R.id.submission_image_card_view)!!
        val thumbnailImageView = view.findViewById<ImageView>(R.id.submission_thumbnail)!!
        val upvoteImageButton = view.findViewById<ImageButton>(R.id.submission_upvote)!!
        val voteCountTextView = view.findViewById<TextView>(R.id.submission_vote_count)!!
        val downvoteImageButton = view.findViewById<ImageButton>(R.id.submission_downvote)!!
        val commentCountTextView = view.findViewById<TextView>(R.id.submission_comment_count)!!

        init {
            if (Matter.accountHelper.isAuthenticated()) {
                upvoteImageButton.visibility = View.VISIBLE
                downvoteImageButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        thumbnailCache.evictAll()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_submission, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.submissionCardViewLayout.visibility = View.INVISIBLE

        with(dataSet[position]) {
            setUsername(holder, this)
            setPostTime(holder, this)
            setType(holder, this)
            setTitle(holder, this)
            setSubreddit(holder, this)
            setFlair(holder, this)
            setThumbnail(holder, this)
            setScore(holder, this)
            setCommentCount(holder, this)
        }

        /** TODO: Investigate loading before the last element is in view */
        if (position == dataSet.size - 1) {
            listener.atBottom()
        }
    }

    private fun setUsername(holder: ViewHolder, submission: Submission) {
        holder.usernameTextView.text =
                holder.usernameTextView.resources.getString(R.string.username_qualifier, submission.author)
    }

    private fun setPostTime(holder: ViewHolder, submission: Submission) {
        with(getTimeDiff(submission.created)) {
            holder.postTimeTextView.text = first.toString().plus(second)
        }
    }

    private fun setType(holder: ViewHolder, submission: Submission) {
        if (!submission.isSelfPost) {
            holder.typeTextView.visibility = View.VISIBLE
            holder.typeSeparator.visibility = View.VISIBLE
            holder.typeTextView.text = submission.postHint
        }
    }

    private fun setTitle(holder: ViewHolder, submission: Submission) {
        holder.titleTextView.text = submission.title
    }

    private fun adjustTitle(holder: ViewHolder, submission: Submission) {
        holder.imageCardView.post {
            val cardViewHeightDp = ceil(holder.imageCardView.height / with(DisplayMetrics()) {
                (holder.imageCardView.context.getSystemService(android.content.Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
                    this
                )
                this
            }.density)
            holder.titleTextView.text = SpannableString(submission.title).apply {
                setSpan(
                    FlowableLeadingMarginSpan2(
                        cardViewHeightDp.toInt() / 20,
                        holder.imageCardView.width + 10
                    ), 0, length, 0
                )
            }
            holder.submissionCardViewLayout.visibility = View.VISIBLE
        }
    }

    private fun setSubreddit(holder: ViewHolder, submission: Submission) {
        holder.subredditTextView.text =
                holder.subredditTextView.context.resources.getString(R.string.subreddit_qualifier, submission.subreddit)
    }

    private fun setFlair(holder: ViewHolder, submission: Submission) {
        if (!submission.linkFlairCssClass.isNullOrBlank()) {
            holder.tagTextView.visibility = View.VISIBLE
            holder.subredditSeparator.visibility = View.VISIBLE
            holder.tagTextView.text = submission.linkFlairCssClass
        }
    }

    private fun setThumbnail(holder: ViewHolder, submission: Submission) {
        if (!submission.isSelfPost && submission.hasThumbnail() && !submission.thumbnail.isNullOrBlank() && submission.thumbnail!!.matches(
                Regex("^http.?://.*")
            )
        ) {
            async {
                val bitmap = with(thumbnailCache.get(submission.uniqueId)) {
                    if (this == null) {
                        BitmapFactory.decodeStream(URL(submission.thumbnail).content as InputStream)
                    } else {
                        Bitmap.createBitmap(this)
                    }
                }
                launch(UI) {
                    if (holder.oldPosition == -1 || holder.oldPosition == holder.layoutPosition) {
                        holder.thumbnailImageView.setImageBitmap(bitmap)
                        holder.imageCardView.visibility = View.VISIBLE
                        adjustTitle(holder, submission)
                        thumbnailCache.put(
                            submission.uniqueId, bitmap
                        )
                    }
                }
            }
        } else {
            holder.imageCardView.visibility = View.GONE
            holder.submissionCardViewLayout.visibility = View.VISIBLE
        }
    }

    private fun setScore(holder: ViewHolder, submission: Submission) {
        if (submission.isScoreHidden) {
            holder.voteCountTextView.text = "?"
        } else {
            holder.voteCountTextView.text = submission.score.toString()
        }
    }

    private fun setCommentCount(holder: ViewHolder, submission: Submission) {
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

    interface SubmissionRecyclerViewListener {
        fun atBottom()
    }
}