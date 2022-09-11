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
object ChatSilenceCommand : ScalaCommand()
{
    @Syntax("[-h]")
    @CommandAlias("unmutechat|unsilencechat")
    @CommandPermission("scstaff.command.mutechat")
    fun onUnMuteChat(
        sender: CommandSender, @Optional hiddenString: String?
    )
    {
        if (!ChatControlService.chatSilenced)
        {
            throw ConditionFailedException("The chat is not silenced. Use ${CC.BOLD}/mutechat${CC.RED} to mute the chat!")
        }

        ChatControlService.chatSilenced = false

        val coloredName = QuickAccess.nameOrConsole(sender)
        val hidden = hiddenString != null && hiddenString == "-h"

        Bukkit.broadcastMessage(
            "${CC.GREEN}The chat has been enabled by ${if (hidden) "staff" else coloredName}${CC.GREEN}."
        )

        StaffMessageStream.send(
            "$coloredName${CC.D_AQUA} un-silenced the chat."
        )
    }

    @Syntax("[-h]")
    @CommandAlias("mutechat|silencechat")
    @CommandPermission("scstaff.command.mutechat")
    fun onMuteChat(
        sender: CommandSender, @Optional hiddenString: String?
    )
    {
        if (ChatControlService.chatSilenced)
        {
            throw ConditionFailedException("The chat is silenced. Use ${CC.BOLD}/unmutechat${CC.RED} to unmute the chat!")
        }

        ChatControlService.chatSilenced = true

        val coloredName = QuickAccess.nameOrConsole(sender)
        val hidden = hiddenString != null && hiddenString == "-h"

        Bukkit.broadcastMessage(
            "${CC.RED}The chat has been disabled by ${if (hidden) "staff" else coloredName}${CC.RED}."
        )

        StaffMessageStream.send(
            "$coloredName${CC.D_AQUA} silenced the chat."
        )
    }
}
