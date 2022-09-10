package gg.scala.staff.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object ToggleStaffMessagesCommand : ScalaCommand()
{
    @CommandAlias("togglestaffmessages|tsm|staffmessages")
    @CommandPermission(ScalaStaffPlugin.STAFF_NODE)
    fun onStaffMessages(player: ScalaPlayer)
    {
        val profile = StaffProfileService.find(player.uniqueId)
            ?: throw ConditionFailedException(
                "Something went terrible wrong while performing this command"
            )
    }
}
