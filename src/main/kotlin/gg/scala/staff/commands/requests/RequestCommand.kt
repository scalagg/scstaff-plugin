package gg.scala.staff.commands.requests

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.player.punishment.category.PunishmentCategory
import gg.scala.staff.requests.RequestProcessor
import net.evilblock.cubed.util.CC
import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.concurrent.CompletableFuture

@AutoRegister
object RequestCommand : ScalaCommand()
{
    @CommandAlias("helpop|request")
    @CommandPermission("scstaff.command.report")
    fun onRequest(player: ScalaPlayer, message: String): CompletableFuture<Void>
    {
        val lemonPlayer = PlayerHandler.find(player.bukkit().uniqueId)
            ?: return CompletableFuture.completedFuture(null)

        val mutePunishment = lemonPlayer.findApplicablePunishment(PunishmentCategory.MUTE)
        if (mutePunishment != null)
        {
            throw ConditionFailedException("You cannot do this while muted!")
        }

        return RequestProcessor
            .getExpiration(player.bukkit())
            .thenCompose {
                if (it != null)
                {
                    player.sendMessage(
                        "${CC.RED}You must wait ${CC.BOLD}${
                            DurationFormatUtils.formatDurationWords(it - System.currentTimeMillis(), true, true)
                        }${CC.RED} before using helpop again."
                    )
                    return@thenCompose CompletableFuture.completedFuture(null)
                }

                player.sendMessage(
                    "${CC.GREEN}We've received your request. This will be looked into as soon as possible."
                )

                RequestProcessor.processRequest(player.bukkit(), message)
            }
    }
}
