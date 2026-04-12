package gg.scala.staff.provider.impl

import gg.scala.lemon.handler.PlayerHandler
import gg.scala.staff.provider.SettingProvider
import org.bukkit.entity.Player

object LemonSettingProvider : SettingProvider
{
    override fun provideSetting(player: Player, setting: String): Boolean
    {
        return PlayerHandler.find(player.uniqueId)?.hasMetadata(setting) ?: false
    }
}
