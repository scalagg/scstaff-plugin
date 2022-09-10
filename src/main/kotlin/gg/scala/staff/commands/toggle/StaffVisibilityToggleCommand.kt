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
object StaffVisibilityToggleCommand : ScalaCommand()
{
    @CommandAlias("togglestaffvisibility|tsv|staffvisibility")
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    fun onStaffVisibility(player: ScalaPlayer): CompletableFuture<Void>
    {
        val profile = StaffProfileService.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "${ScalaStaffPlugin.PREFIX_PLUGIN} Something went terrible wrong while performing this command"
            )

        profile.staffVisibilityEnabled = !profile.staffVisibilityEnabled

        return profile.save()
            .thenRun {
                player.sendMessage(
                    if (profile.staffVisibilityEnabled)
                    {
                        "${ChatColor.GREEN}You are now viewing staff."
                    } else
                    {
                        "${ChatColor.RED}You are no longer viewing staff."
                    }
                )
            }
    }
}
