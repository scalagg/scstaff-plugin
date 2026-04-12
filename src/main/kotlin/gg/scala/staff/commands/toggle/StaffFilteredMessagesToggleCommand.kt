package gg.scala.staff.commands.toggle

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.player.metadata.Metadata
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import org.bukkit.ChatColor
import java.util.concurrent.CompletableFuture

@AutoRegister
object StaffFilteredMessagesToggleCommand : ScalaCommand()
{
    @CommandAlias("togglefilteredmessages|tfm|filteredmessages")
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    fun onFilteredMessages(player: ScalaPlayer): CompletableFuture<Void>
    {
        val profile = PlayerHandler.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "${ScalaStaffPlugin.PREFIX_PLUGIN} Something went terrible wrong while performing this command"
            )

        if (profile.hasMetadata("filter-disabled"))
        {
            profile.removeMetadata("filter-disabled")
        } else
        {
            profile.updateOrAddMetadata("filter-disabled", Metadata(true))
        }

        return profile.save()
            .thenRun {
                player.sendMessage(
                    if (!profile.hasMetadata("filter-disabled"))
                    {
                        "${ChatColor.GREEN}You are now receiving filtered message notifications."
                    } else
                    {
                        "${ChatColor.RED}You are no longer receiving filtered message notifications."
                    }
                )
            }
    }
}
