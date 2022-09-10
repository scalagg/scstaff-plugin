package gg.scala.staff.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.moderation.ModerationService
import gg.scala.staff.notify

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object StaffModeCommand : ScalaCommand()
{
    @CommandPermission("scstaff.command.staffmode")
    @CommandAlias("staffmode|staff|sm|modmode|mod|mm|h")
    fun onStaffMode(player: ScalaPlayer)
    {
        if (player.bukkit().hasMetadata("mod-mode"))
        {
            ModerationService.leaveModMode(player.bukkit())
            notify(player.bukkit(), "left modmode")
            return
        }

        ModerationService.enterModMode(player.bukkit())
        notify(player.bukkit(), "entered modmode")
    }
}
