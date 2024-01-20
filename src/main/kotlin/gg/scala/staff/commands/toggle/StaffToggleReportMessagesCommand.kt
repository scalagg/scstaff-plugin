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
object StaffToggleReportMessagesCommand : ScalaCommand()
{
    @CommandAlias("togglereportmessages|trm|reportmessages")
    @CommandPermission("scstaff.staff-member")
    fun onStaffMessages(player: ScalaPlayer): CompletableFuture<Void>
    {
        val profile = StaffProfileService.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "${ScalaStaffPlugin.PREFIX_PLUGIN} Something went terrible wrong while performing this command"
            )

        profile.staffReportNotificationsEnabled = !profile.staffReportNotificationsEnabled

        return profile.save()
            .thenRun {
                player.sendMessage(
                    if (profile.staffReportNotificationsEnabled)
                    {
                        "${ChatColor.GREEN}You will now receive report messages."
                    } else
                    {
                        "${ChatColor.RED}You are no receiving report messages."
                    }
                )
            }
    }
}
