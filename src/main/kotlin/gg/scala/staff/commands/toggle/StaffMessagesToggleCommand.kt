package gg.scala.staff.commands.toggle

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import org.bukkit.ChatColor
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object StaffMessagesToggleCommand : ScalaCommand()
{
    @CommandAlias("togglestaffmessages|tsm|staffmessages")
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    fun onStaffMessages(player: ScalaPlayer): CompletableFuture<Void>
    {
        val profile = StaffProfileService.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "${ScalaStaffPlugin.PREFIX_PLUGIN} Something went terrible wrong while performing this command"
            )

        if (profile.staffChannel != null)
        {
            throw ConditionFailedException("You cannot disable staff messages as you're in a staff channel!")
        }

        profile.staffMessagesEnabled = !profile.staffMessagesEnabled

        return profile.save()
            .thenRun {
                player.sendMessage(
                    if (profile.staffMessagesEnabled)
                    {
                        "${ChatColor.GREEN}You are now receiving staff messages."
                    } else
                    {
                        "${ChatColor.RED}You are no longer receiving staff messages."
                    }
                )
            }
    }
}
