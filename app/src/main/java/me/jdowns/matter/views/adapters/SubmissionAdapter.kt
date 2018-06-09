package me.jdowns.matter.views.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.UiThread
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
import me.jdowns.matter.styles.FlowableLeadingMarginSpan2
import me.jdowns.matter.utils.BitmapLruCache
import me.jdowns.matter.views.widgets.BaseRecyclerView
import net.dean.jraw.models.Submission
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class SubmissionAdapter(private val dataSet: List<Submission>) :
    BaseRecyclerView.BaseAdapter<SubmissionAdapter.ViewHolder, Submission>(dataSet) {
    private val thumbnailCache = BitmapLruCache()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val submissionViewLayout = view.findViewById<ViewGroup>(R.id.submission_view_layout)!!
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
        val submissionContentLayout = view.findViewById<ViewGroup>(R.id.view_submission_content)!!
        val submissionActionsLayout = view.findViewById<ViewGroup>(R.id.view_submission_actions)!!

        init {
            if (Matter.isRealUser()) {
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
        holder.submissionViewLayout.visibility = View.INVISIBLE

        with(holder.submissionActionsLayout) {
            alpha = 0.0f
            holder.submissionContentLayout.setOnLongClickListener {
                animate().let {
                    if (visibility == View.GONE) {
                        it.withStartAction { visibility = View.VISIBLE }
                        it.alpha(1.0f)
                    } else {
                        it.withEndAction { visibility = View.GONE }
                        it.alpha(0.0f)
                    }
                }.start()
                true
            }
        }

        kotlin.run {
            val submission = dataSet[position]
            setUsername(holder, submission)
            setPostTime(holder, submission)
            setType(holder, submission)
            setTitle(holder, submission)
            setSubreddit(holder, submission)
            setFlair(holder, submission)
            launch(UI) {
                setThumbnail(holder, submission)
            }
            setScore(holder, submission)
            setCommentCount(holder, submission)
        }

        super.onBindViewHolder(holder, position)
    }

    private fun setUsername(holder: ViewHolder, submission: Submission) {
        holder.usernameTextView.text =
                holder.usernameTextView.resources.getString(R.string.username_qualifier, submission.author)
    }

    private fun setPostTime(holder: ViewHolder, submission: Submission) {
        holder.postTimeTextView.text = getTimeDiffString(submission.created)
    }

    private fun setType(holder: ViewHolder, submission: Submission) {
        if (!submission.isSelfPost) {
            with(holder) {
                typeSeparator.visibility = View.VISIBLE
                with(typeTextView) {
                    visibility = View.VISIBLE
                    text = submission.postHint
                }
            }
        }
    }

    private fun setTitle(holder: ViewHolder, submission: Submission) {
        holder.titleTextView.text = submission.title
    }

    private fun adjustTitle(holder: ViewHolder, submission: Submission) {
        with(holder) {
            imageCardView.post {
                val cardViewHeightDp = ceil(imageCardView.height / DisplayMetrics().also {
                    (imageCardView.context.getSystemService(android.content.Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
                        it
                    )
                }.density)
                titleTextView.text = SpannableString(submission.title).also {
                    it.setSpan(
                        FlowableLeadingMarginSpan2(
                            cardViewHeightDp.toInt() / 20,
                            imageCardView.width + 10
                        ), 0, it.length, 0
                    )
                }
                submissionViewLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setSubreddit(holder: ViewHolder, submission: Submission) {
        holder.subredditTextView.text =
                holder.subredditTextView.context.resources.getString(R.string.subreddit_qualifier, submission.subreddit)
    }

    private fun setFlair(holder: ViewHolder, submission: Submission) {
        if (!submission.linkFlairCssClass.isNullOrBlank()) {
            with(holder) {
                subredditSeparator.visibility = View.VISIBLE
                with(tagTextView) {
                    visibility = View.VISIBLE
                    text = submission.linkFlairCssClass
                }
            }
        }
    }

    @UiThread
    private suspend fun setThumbnail(holder: ViewHolder, submission: Submission) {
        if (!submission.isSelfPost && submission.hasThumbnail() && !submission.thumbnail.isNullOrBlank() && submission.thumbnail!!.matches(
                Regex("^http.?://.*")
            )
        ) {
            val bitmap = thumbnailCache.get(submission.uniqueId)?.run {
                Bitmap.createBitmap(this)
            } ?: async {
                BitmapFactory.decodeStream(URL(submission.thumbnail).content as InputStream)
            }.await()
            with(holder) {
                if (oldPosition == -1 || oldPosition == layoutPosition) {
                    thumbnailImageView.setImageBitmap(bitmap)
                    imageCardView.visibility = View.VISIBLE
                    adjustTitle(this, submission)
                    thumbnailCache.put(submission.uniqueId, bitmap)
                }
            }
        } else {
            with(holder) {
                imageCardView.visibility = View.GONE
                submissionViewLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setScore(holder: ViewHolder, submission: Submission) {
        holder.voteCountTextView.text = if (submission.isScoreHidden) "?" else truncateNumber(submission.score)
    }

    private fun setCommentCount(holder: ViewHolder, submission: Submission) {
        holder.commentCountTextView.text = truncateNumber(submission.commentCount)
    }

    private fun getTimeDiffString(submissionDate: Date): String = kotlin.run {
        val timeDiffSeconds = (Date().time - submissionDate.time) / 1000L
        val timeUnit = TimeUnit.SECONDS
        when {
            timeDiffSeconds < 60 -> timeDiffSeconds.toString().plus("s")
            timeDiffSeconds < 3600 -> TimeUnit.MINUTES.convert(timeDiffSeconds, timeUnit).toString().plus("m")
            timeDiffSeconds < 86400 -> TimeUnit.HOURS.convert(timeDiffSeconds, timeUnit).toString().plus("h")
            timeDiffSeconds < 31536000 -> TimeUnit.DAYS.convert(timeDiffSeconds, timeUnit).toString().plus("d")
            else -> (TimeUnit.DAYS.convert(timeDiffSeconds, timeUnit) / 365).toString().plus("y")
        }
    }

    private fun truncateNumber(number: Int) = with(number) {
        when {
            number > 1000 -> number.toString().dropLast(3).plus("k")
            number > 1000000 -> number.toString().dropLast(6).plus("m")
            else -> number.toString()
        }
    }
}