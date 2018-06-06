package me.jdowns.matter.dagger

import dagger.Component
import dagger.android.AndroidInjectionModule
import me.jdowns.matter.dagger.modules.RoomModule
import me.jdowns.matter.views.activities.main.MainPresenter
import me.jdowns.matter.views.activities.oauth.OAuthPresenter
import me.jdowns.matter.views.fragments.logindialog.LogInDialogPresenter
import me.jdowns.matter.views.fragments.profilebottomsheetdialog.ProfileBottomSheetDialogPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (RoomModule::class)])
interface ApplicationComponent {
    fun inject(mainPresenter: MainPresenter)
    fun inject(oAuthPresenter: OAuthPresenter)
    fun inject(logInDialogPresenter: LogInDialogPresenter)
    fun inject(profileBottomSheetDialogPresenter: ProfileBottomSheetDialogPresenter)
}