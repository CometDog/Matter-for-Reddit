package me.jdowns.matter.views.widgets

import android.content.Context
import android.support.design.widget.NavigationView
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

class ScrollableNavigationView(context: Context, attributeSet: AttributeSet) : NavigationView(context, attributeSet) {
    private var lastAnimation: Animation? = null
    private val animationLength = 500L

    override fun startAnimation(animation: Animation?) {
        if (lastAnimation == null || lastAnimation!!.hasEnded()) {
            lastAnimation = animation
            super.startAnimation(animation)
        }
    }

    fun tryStartHideAnimation() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
            startAnimation(TranslateAnimation(x, x, y, -height.toFloat() - 10).apply {
                duration = animationLength
            })
        }
    }

    fun tryStartShowAnimation() {
        if (visibility == View.GONE) {
            visibility = View.VISIBLE
            startAnimation(TranslateAnimation(x, x, y - height.toFloat() - 10, y).apply {
                duration = animationLength
            })
        }
    }
}