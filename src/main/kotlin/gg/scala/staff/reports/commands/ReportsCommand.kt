package gg.scala.staff.reports.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.reports.ReportProcessor
import gg.scala.staff.reports.ViewReportsMenu

/**
 * @author GrowlyX
 * @since 1/16/2024
 */
@AutoRegister
object ReportsCommand : ScalaCommand()
{
    @CommandPermission("scstaff.command.reports")
    @CommandAlias("reports|staffreports|viewreports")
    fun onReports(player: ScalaPlayer) = ReportProcessor
        .getAllReports()
        .thenAccept {
            ViewReportsMenu(it).openMenu(player.bukkit())
        }
}
