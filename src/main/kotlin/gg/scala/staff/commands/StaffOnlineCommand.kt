package gg.scala.staff.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.staff.network.NetworkOnlineStaffMenu
import gg.scala.staff.ScalaStaffPlugin
import org.bukkit.entity.Player

@AutoRegister
object StaffOnlineCommand : ScalaCommand()
{
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    @CommandAlias("onlinestaff|os|stafflist")
    fun onOnlineStaff(player: Player)
    {
        NetworkOnlineStaffMenu().openMenu(player)
    }
}
