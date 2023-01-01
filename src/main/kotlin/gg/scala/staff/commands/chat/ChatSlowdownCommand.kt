package gg.scala.staff.commands.chat

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.acf.annotation.Syntax
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.moderation.chat.ChatControlService
import gg.scala.staff.stream.StaffMessageStream
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object ChatSlowdownCommand : ScalaCommand()
{
    @Syntax("<seconds> [-h]")
    @CommandAlias("slowchat|chatslow|chatcooldown|chatcd|slow")
    @CommandPermission("sctaff.command.slowchat")
    fun onSlowChat(
        sender: CommandSender, seconds: Int, @Optional hiddenString: String?
    )
    {
        if (ChatControlService.chatSlowdown > 0)
        {
            throw ConditionFailedException("The chat is already slowed. Use ${CC.BOLD}/unslowchat${CC.RED} to un-slow the chat!")
        }

        if (seconds <= 0)
        {
            throw ConditionFailedException("You must choose a number greater than ${CC.BOLD}0 seconds${CC.RED}!")
        }

        ChatControlService.chatSlowdown = seconds

        val coloredName = QuickAccess.nameOrConsole(sender)
        val hidden = hiddenString != null && hiddenString == "-h"
        val amount = "$seconds second${if (seconds == 1) "" else "s"}"

        Bukkit.broadcastMessage(
            "${CC.GREEN}The chat has been slowed to $amount by ${if (hidden) "staff" else coloredName}${CC.GREEN}."
        )

        StaffMessageStream.send(
            "$coloredName slowed the chat to $amount."
        )
    }

    @Syntax("<seconds> [-h]")
    @CommandAlias("unslowchat|removechatcooldown|rcc|usc")
    @CommandPermission("sctaff.command.unslowchat")
    fun onUnSlowChat(
        sender: CommandSender, @Optional hiddenString: String?
    )
    {
        if (ChatControlService.chatSlowdown <= 0)
        {
            throw ConditionFailedException("The chat is not yet slowed. Use ${CC.BOLD}/slowchat${CC.RED} to slow the chat!")
        }

        ChatControlService.chatSlowdown = 0

        val coloredName = QuickAccess.nameOrConsole(sender)
        val hidden = hiddenString != null && hiddenString == "-h"

        Bukkit.broadcastMessage(
            "${CC.GREEN}The chat slowdown has been removed by ${if (hidden) "staff" else coloredName}${CC.GREEN}."
        )

        StaffMessageStream.send(
            "$coloredName un-slowed the chat."
        )
    }
}
