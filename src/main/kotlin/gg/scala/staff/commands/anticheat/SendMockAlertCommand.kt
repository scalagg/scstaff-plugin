package gg.scala.staff.commands.anticheat

import dev.sim0n.anticheat.check.CheckType
import dev.sim0n.anticheat.event.AntiCheatAlertEvent
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 12/23/2023
 */
@AutoRegister
@CommandAlias("sendmockalert")
@CommandPermission("op")
object SendMockAlertCommand : ScalaCommand()
{
    @Default
    fun onDefault(player: ScalaPlayer)
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("anticheat"))
        {
            throw ConditionFailedException(
                "No anticheat plugin is loaded on this server."
            )
        }

        Bukkit.getPluginManager().callEvent(
            AntiCheatAlertEvent(
                player.bukkit(),
                CheckType.DOUBLE_CLICK,
                "test",
                2.0
            )
        )
    }
}
