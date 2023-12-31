package gg.scala.staff.commands.reports

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.lemon.player.LemonPlayer
import gg.scala.staff.config
import gg.scala.staff.reports.ReportMenu
import gg.scala.staff.reports.ReportProcessor
import net.evilblock.cubed.util.CC
import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object ReportCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: ExtendedScalaPlugin

    @CommandAlias("report")
    @CommandCompletion("@players")
    @CommandPermission("scstaff.command.report")
    fun onReport(
        player: ScalaPlayer, target: LemonPlayer,
        @Optional message: String?
    ): CompletableFuture<Void>
    {
        if (target.bukkitPlayer!!.uniqueId == player.uniqueId)
        {
            throw ConditionFailedException("You're not allowed to report yourself.")
        }

        if (target.hasPermission("scstaff.command.report.bypass"))
        {
            throw ConditionFailedException("You're unable to report ${CC.SEC}${target.getColoredName()}${CC.RED}.")
        }

        return ReportProcessor
            .getExpiration(player.bukkit())
            .thenCompose {
                if (it != null)
                {
                    player.sendMessage(
                        "${CC.RED}You must wait ${CC.BOLD}${
                            DurationFormatUtils.formatDurationWords(it - System.currentTimeMillis(), true, true)
                        }${CC.RED} before reporting again."
                    )
                    return@thenCompose CompletableFuture.completedFuture(null)
                }

                plugin.flavor()
                    .injected<ReportMenu>(target.bukkitPlayer!!)
                    .openMenu(player.bukkit())

                return@thenCompose CompletableFuture.completedFuture(null)
            }
    }
}
