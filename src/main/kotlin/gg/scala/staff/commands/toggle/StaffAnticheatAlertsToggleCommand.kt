package gg.scala.staff.commands.toggle

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import org.bukkit.ChatColor
import java.util.concurrent.CompletableFuture

@AutoRegister
object StaffAnticheatAlertsToggleCommand : ScalaCommand()
{
    @CommandAlias("toggleanticheatalerts|taa|anticheatalerts|acalerts|alerts|toggleglobalalerts")
    @CommandPermission("scstaff.command.anticheatalerts")
    fun onAnticheatAlerts(player: ScalaPlayer): CompletableFuture<Void>
    {
        val profile = StaffProfileService.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "${ScalaStaffPlugin.PREFIX_PLUGIN} Something went terrible wrong while performing this command"
            )

        profile.staffGlobalAntiCheatAlerts = !(profile.staffGlobalAntiCheatAlerts ?: false)

        return profile.save()
            .thenRun {
                player.sendMessage(
                    if (profile.staffGlobalAntiCheatAlerts == true)
                    {
                        "${ChatColor.GREEN}You will now receive global anticheat alerts."
                    } else
                    {
                        "${ChatColor.RED}You are no longer receive global anticheat alerts."
                    }
                )
            }
    }
}
