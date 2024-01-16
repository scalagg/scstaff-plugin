package gg.scala.staff.reports.commands

import gg.scala.commons.acf.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.staff.reports.ReportProcessor
import gg.scala.staff.reports.ViewReportsMenu

/**
 * @author GrowlyX
 * @since 1/16/2024
 */
@AutoRegister
@CommandPermission("scstaff.command.reports")
@CommandAlias("reports|staffreports|viewreports")
object ReportsCommand : ScalaCommand()
{
    @Default
    fun onReports(player: ScalaPlayer) = ReportProcessor
        .getAllReports()
        .thenAccept {
            ViewReportsMenu(it).openMenu(player.bukkit())
        }

    @Subcommand("for")
    @CommandCompletion("@players")
    fun onReports(player: ScalaPlayer, target: AsyncLemonPlayer) = target
        .validatePlayers(player.bukkit(), false) {
            ReportProcessor
                .getAllReportsOnUser(it.uniqueId)
                .thenAccept { reports ->
                    ViewReportsMenu(reports, "for ${it.name}")
                        .openMenu(player.bukkit())
                }
                .join()
        }
}
