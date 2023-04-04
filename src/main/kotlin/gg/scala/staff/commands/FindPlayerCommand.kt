package gg.scala.staff.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.ScalaStaffPlugin
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent

/**
 * @author GrowlyX
 * @since 4/3/2023
 */
@AutoRegister
object FindPlayerCommand : ScalaCommand()
{
    @CommandAlias("find")
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    fun onFind(player: ScalaPlayer, target: AsyncLemonPlayer) =
        target.validatePlayers(player.bukkit(), false) {
            QuickAccess
                .server(it.uniqueId)
                .thenAccept { server ->
                    if (server == null)
                    {
                        throw ConditionFailedException(
                            "We could not find ${it.name} on the network!"
                        )
                    }

                    player.sendMessage(
                        FancyMessage()
                            .withMessage(
                                "${CC.YELLOW}${it.name}${CC.GRAY} is logged into ${CC.WHITE}${server.id}${CC.GRAY}."
                            ),
                        FancyMessage()
                            .withMessage("${CC.GREEN}[Jump]")
                            .andHoverOf("${CC.GREEN}Jump to their server!")
                            .andCommandOf(
                                ClickEvent.Action.RUN_COMMAND,
                                "/jump ${it.name}"
                            )
                            .withMessage(" ")
                            .withMessage("${CC.GOLD}[Kick]")
                            .andHoverOf("${CC.GOLD}Kick the player!")
                            .andCommandOf(
                                ClickEvent.Action.RUN_COMMAND,
                                "/kick ${it.name}"
                            )
                            .withMessage(" ")
                            .withMessage("${CC.YELLOW}[Message]")
                            .andHoverOf("${CC.YELLOW}Message the player!")
                            .andCommandOf(
                                ClickEvent.Action.RUN_COMMAND,
                                "/message ${it.name} silly player"
                            )
                    )
                }
                .join()
        }
}
