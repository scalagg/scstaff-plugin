package gg.scala.staff.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.lemon.redirection.PlayerRedirectSystem
import gg.scala.lemon.redirection.impl.VelocityRedirectSystem
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.stream.StaffMessageStream
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object StaffJumpCommand : ScalaCommand()
{
    @CommandAlias("jump|jtp|jumptoplayer")
    @CommandCompletion("@players")
    @CommandPermission("scstaff.command.jump")
    fun onJump(player: Player, target: AsyncLemonPlayer): CompletableFuture<Void>
    {
        return target.validatePlayers(player, false) { lemonPlayer ->
            QuickAccess.server(lemonPlayer.uniqueId)
                .thenAcceptAsync {
                    if (it == null)
                    {
                        throw ConditionFailedException(
                            "No player matching ${CC.YELLOW}${lemonPlayer.name}${CC.RED} was found on the network."
                        )
                    }

                    val coloredName = QuickAccess
                        .fetchColoredName(lemonPlayer.uniqueId)

                    StaffMessageStream.send(
                        "${QuickAccess.originalRank(player).color}${player.uniqueId.username()} ${CC.D_AQUA}jumped to ${CC.AQUA}${coloredName}${CC.D_AQUA}.",
                        includeOrigin = false
                    )

                    VelocityRedirectSystem.redirect(player, it.id, mapOf(
                        "teleporting-player" to lemonPlayer.uniqueId.toString()
                    ))
                }
                .join()
        }
    }
}
