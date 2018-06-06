package me.jdowns.matter.views.fragments.profilebottomsheetdialog

import me.jdowns.matter.views.fragments.logindialog.LogInDialogFragment

class ProfileBottomSheetDialogNavigator(private val view: ProfileBottomSheetDialogFragment) {
    fun showLogInDialogView() = LogInDialogFragment().show(view.childFragmentManager, LogInDialogFragment.FRAGMENT_TAG)
}