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
import gg.scala.lemon.player.enums.PermissionCheck
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.config
import gg.scala.staff.reports.ReportMenu
import gg.scala.staff.reports.ReportProcessor
import net.evilblock.cubed.util.CC
import org.apache.commons.lang3.time.DurationFormatUtils
import org.bukkit.entity.Player
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
    fun onReport(player: ScalaPlayer, target: AsyncLemonPlayer) = ReportProcessor
        .getExpiration(player.bukkit())
        .thenCompose {
            if (it != null)
            {
                throw ConditionFailedException(
                    "${CC.RED}You must wait ${CC.BOLD}${
                        DurationFormatUtils.formatDurationWords(it - System.currentTimeMillis(), true, true)
                    }${CC.RED} before reporting again."
                )
            }

            return@thenCompose target
                .validatePlayers(player.bukkit(), true) { lemonPlayer ->
                    val online = QuickAccess
                        .online(lemonPlayer.uniqueId)
                        .join()

                    if (!online)
                    {
                        throw ConditionFailedException(
                            "The player ${CC.YELLOW}${lemonPlayer.name}${CC.RED} is not logged onto the network."
                        )
                    }

                    if (lemonPlayer.uniqueId == player.uniqueId)
                    {
                        throw ConditionFailedException("You're not allowed to report yourself.")
                    }

                    if (lemonPlayer.hasPermission("scstaff.command.report.bypass", checkType = PermissionCheck.COMPOUNDED))
                    {
                        throw ConditionFailedException("You're unable to report ${CC.YELLOW}${lemonPlayer.name}${CC.RED}.")
                    }

                    plugin.flavor()
                        .injected<ReportMenu>(lemonPlayer.uniqueId)
                        .openMenu(player.bukkit())
                }
        }
}
