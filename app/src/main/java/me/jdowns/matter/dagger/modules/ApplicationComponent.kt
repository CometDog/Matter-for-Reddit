package me.jdowns.matter.dagger.modules

import dagger.Component
import me.jdowns.matter.Matter
import me.jdowns.matter.views.activities.MainActivity
import me.jdowns.matter.views.activities.OAuthActivity
import me.jdowns.matter.views.fragments.LogInDialogFragment
import me.jdowns.matter.views.fragments.ProfileBottomSheetDialogFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [(RoomModule::class)])
interface ApplicationComponent {
    fun inject(matter: Matter)
    fun inject(mainActivity: MainActivity)
    fun inject(oAuthActivity: OAuthActivity)
    fun inject(logInDialogFragment: LogInDialogFragment)
    fun inject(profileBottomSheetDialogFragment: ProfileBottomSheetDialogFragment)
}