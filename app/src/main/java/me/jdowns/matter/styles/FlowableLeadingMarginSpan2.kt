package me.jdowns.matter.styles

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

class FlowableLeadingMarginSpan2(private val _lines: Int, private val _margin: Int) :
    LeadingMarginSpan.LeadingMarginSpan2 {
    override fun getLeadingMargin(first: Boolean): Int = if (first) {
        _margin
    } else {
        0
    }

    override fun getLeadingMarginLineCount(): Int = _lines

    override fun drawLeadingMargin(
        c: Canvas?, p: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
        text: CharSequence?, start: Int, end: Int, first: Boolean, layout: Layout?
    ) {
    }
}