package gg.scala.staff.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.handler.PunishmentHandler
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.staff.warning.WarningHandler
import gg.scala.staff.warning.WarningMenu
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object StaffWarnCommand : ScalaCommand()
{
    @CommandCompletion("@players")
    @CommandAlias("warnings|warns")
    @CommandPermission("scstaff.command.warn")
    fun onWarnings(
        player: ScalaPlayer, target: AsyncLemonPlayer
    ): CompletableFuture<Void>
    {
        return target.validatePlayers(
            player.bukkit(), false
        ) {
            Tasks.sync {
                WarningMenu(it.uniqueId).openMenu(player.bukkit())
            }
        }
    }

    @CommandAlias("warn")
    @CommandCompletion("@players")
    @CommandPermission("scstaff.command.warn")
    fun onWarn(
        sender: ScalaPlayer,
        target: AsyncLemonPlayer,
        reason: String
    ): CompletableFuture<Void>
    {
        return target.validatePlayers(
            sender.bukkit(), false
        ) {
            WarningHandler
                .warn(
                    sender.bukkit(), it.uniqueId, reason
                )
                .join()
        }
    }
}
