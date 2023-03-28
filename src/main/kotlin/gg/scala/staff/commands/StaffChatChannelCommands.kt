package gg.scala.staff.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.channel.StaffChatChannelType
import gg.scala.staff.profile.StaffProfileService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object StaffChatChannelCommands : ScalaCommand()
{
    // TODO: DYNAMIC SOLUTION FOR THIS MADNESS
    @CommandAlias("ownerchat|oc|o")
    @CommandPermission("scstaff.channel.owner")
    fun onOwnerChat(
        player: ScalaPlayer, @Optional message: String?
    )
    {
        performStaffChannel(
            player, StaffChatChannelType.Owner, message
        )
    }

    @CommandAlias("developerchat|dc|d")
    @CommandPermission("scstaff.channel.developer")
    fun onDeveloperChat(
        player: ScalaPlayer, @Optional message: String?
    )
    {
        performStaffChannel(
            player, StaffChatChannelType.Developer, message
        )
    }

    @CommandAlias("staffchat|sc|s")
    @CommandPermission("scstaff.channel.staff")
    fun onStaffChat(
        player: ScalaPlayer, @Optional message: String?
    )
    {
        performStaffChannel(
            player, StaffChatChannelType.Staff, message
        )
    }

    @CommandAlias("adminchat|ac|a")
    @CommandPermission("scstaff.channel.admin")
    fun onAdminChat(
        player: ScalaPlayer, @Optional message: String?
    )
    {
        performStaffChannel(
            player, StaffChatChannelType.Admin, message
        )
    }

    private fun performStaffChannel(
        player: ScalaPlayer, type: StaffChatChannelType, message: String?
    )
    {
        val profile = StaffProfileService.find(player.bukkit())
            ?: throw ConditionFailedException(
                "${ScalaStaffPlugin.PREFIX_PLUGIN} Something went terribly wrong while performing this command"
            )

        if (!profile.staffMessagesEnabled)
        {
            throw ConditionFailedException("You cannot enter a staff channel if you have staff messages disabled.")
        }

        if (message.isNullOrEmpty())
        {
            if (profile.staffChannel == type)
            {
                profile.staffChannel = null
                profile.save()

                player.sendMessage(
                    Component
                        .text("You're no longer chatting in the ${type.name.lowercase()} channel.")
                        .color(NamedTextColor.RED)
                )
                return
            }

            profile.staffChannel = type
            profile.save()

            player.sendMessage(
                Component
                    .text("You're now chatting in the ${type.name.lowercase()} channel.")
                    .color(NamedTextColor.GREEN)
            )
            return
        }

        QuickAccess.sendChannelMessage(
            type.name.lowercase(), message,
            PlayerHandler.find(player.uniqueId)!!
        )
    }
}
