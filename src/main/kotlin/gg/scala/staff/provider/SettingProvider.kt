package gg.scala.staff.provider

import org.bukkit.entity.Player

interface SettingProvider
{
    fun provideSetting(player: Player, setting: String): Boolean
}
