package me.jdowns.matter.views.widgets

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class BaseBottomSheetDialog(context: Context, @StyleRes theme: Int) : BottomSheetDialog(context, theme) {
    lateinit var view: View

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        this.view = view
        super.setContentView(this.view, params)
    }

    override fun setContentView(view: View) {
        setContentView(view, null)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        setContentView(LayoutInflater.from(context).inflate(layoutResID, null, false))
    }

    fun getContentView(): View {
        return this.view
    }
}