package gg.scala.staff.commands.reports

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.LemonPlayer
import gg.scala.staff.config
import gg.scala.staff.reports.ReportMenu
import gg.scala.staff.reports.ReportProcessor
import net.evilblock.cubed.util.CC
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object ReportCommand : ScalaCommand()
{
    @CommandAlias("report")
    @CommandPermission("scstaff.command.report")
    fun onReport(
        player: ScalaPlayer, target: LemonPlayer,
        @Optional message: String?
    )
    {
        val coolDowns = ReportProcessor.coolDowns

        if (!coolDowns.test(player.uniqueId))
        {
            val remaining = coolDowns
                .remainingTime(
                    player.uniqueId,
                    TimeUnit.MILLISECONDS
                ) / 1000.0F

            player.sendMessage(
                "${CC.RED}You must wait ${CC.BOLD}${
                    "%.2f".format(remaining)
                } second${
                    if (remaining == 1.00F) "" else "s"
                }${CC.RED} before reporting again."
            )
            return
        }

        if (config().reportsUiBased)
        {
            ReportMenu(target.bukkitPlayer!!)
                .openMenu(player.bukkit())
            return
        }

        if (message.isNullOrEmpty())
        {
            // wonky fr
            throw ConditionFailedException(
                "${CC.SEC}Usage: ${CC.PRI}/report ${CC.WHITE}<target> <message>"
            )
        }

        ReportProcessor.processReport(
            player.bukkit(), target.uniqueId, message
        )
    }
}
