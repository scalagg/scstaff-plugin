package gg.scala.staff.provider.impl

import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.basics.plugin.settings.defaults.values.StateSettingValue
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.provider.SettingProvider
import org.bukkit.entity.Player

@Service
@IgnoreAutoScan
@SoftDependency("ScBasics")
object BasicsSettingProvider : SettingProvider
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    @Configure
    fun configure()
    {
        plugin.settingProvider = this
    }

    override fun provideSetting(player: Player, setting: String): Boolean
    {
        val profile = BasicsProfileService.find(player)
            ?: return false

        return profile.setting(setting, StateSettingValue.ENABLED) == StateSettingValue.DISABLED
    }
}
