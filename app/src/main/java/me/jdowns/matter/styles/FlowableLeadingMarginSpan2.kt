package me.jdowns.matter.styles

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

class FlowableLeadingMarginSpan2(private val lines: Int, private val margin: Int) :
    LeadingMarginSpan.LeadingMarginSpan2 {
    override fun getLeadingMargin(first: Boolean): Int = if (first) margin else 0

    override fun getLeadingMarginLineCount(): Int = lines

    override fun drawLeadingMargin(
        c: Canvas?, p: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
        text: CharSequence?, start: Int, end: Int, first: Boolean, layout: Layout?
    ) {
    }
}