package me.jdowns.matter.views.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
    lateinit var listener: SubmissionRecyclerViewListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
            async(UI) {
                holder.thumbnailImageView.setImageBitmap(
                    async {
                        val bitmapFromCache = getBitmapFromCache(submission.thumbnail!!)
                        if (bitmapFromCache == null) {
                            val newBitmap = BitmapFactory.decodeStream(
                                URL(submission.thumbnail).content as InputStream
                            )
                            addBitmapToCache(
                                submission.thumbnail!!, newBitmap
                            )
                            newBitmap
                        } else {
                            Bitmap.createBitmap(bitmapFromCache)
                        }
                    }.await()
                )
                holder.imageCardView.visibility = View.VISIBLE
            }
        } else {
            holder.imageCardView.visibility = View.GONE
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

    companion object {
        /** TODO: Cache based on memory usage instead of cache size */
        private val thumbnailCache = LruCache<String, Bitmap>(25)
        private fun addBitmapToCache(key: String, bitmap: Bitmap) {
            if (getBitmapFromCache(key) == null) {
                thumbnailCache.put(key, bitmap)
            }
        }

        private fun getBitmapFromCache(key: String): Bitmap? {
            return thumbnailCache.get(key) ?: null
        }
    }
}