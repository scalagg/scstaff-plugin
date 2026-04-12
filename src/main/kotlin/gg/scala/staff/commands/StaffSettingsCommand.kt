package gg.scala.staff.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.moderation.ModerationService
import gg.scala.staff.notify
import gg.scala.staff.settings.StaffSettingsMenu

@AutoRegister
object StaffSettingsCommand : ScalaCommand()
{
    @CommandAlias("staffsettings|staffoptions")
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    fun onStaffSettings(player: ScalaPlayer)
    {
        StaffSettingsMenu().openMenu(player.bukkit())
    }
}
