package me.jdowns.matter.dagger

import dagger.Component
import dagger.android.AndroidInjectionModule
import me.jdowns.matter.dagger.modules.RoomModule
import me.jdowns.matter.views.activities.OAuthActivity
import me.jdowns.matter.views.activities.main.MainPresenter
import me.jdowns.matter.views.fragments.LogInDialogFragment
import me.jdowns.matter.views.fragments.ProfileBottomSheetDialogFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (RoomModule::class)])
interface ApplicationComponent {
    fun inject(mainPresenter: MainPresenter)
    fun inject(oAuthActivity: OAuthActivity)
    fun inject(logInDialogFragment: LogInDialogFragment)
    fun inject(profileBottomSheetDialogFragment: ProfileBottomSheetDialogFragment)
}