package me.jdowns.matter.views.widgets

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): BaseBottomSheetDialog {
        return BaseBottomSheetDialog(context!!, theme)
    }

    override fun onStart() {
        super.onStart()
        onDialogViewCreated((dialog as BaseBottomSheetDialog).getContentView(), arguments)
    }

    abstract fun onDialogViewCreated(view: View, savedInstanceState: Bundle?)
}