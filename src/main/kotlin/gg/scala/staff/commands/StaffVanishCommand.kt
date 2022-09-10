package gg.scala.staff.commands

import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.LemonConstants
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.player.LemonPlayer
import gg.scala.lemon.util.QuickAccess.coloredName
import gg.scala.commons.acf.BaseCommand
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.*
import gg.scala.commons.acf.bukkit.contexts.OnlinePlayer
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.moderation.ModerationService
import gg.scala.staff.notify
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/15/2021
 */
@AutoRegister
object StaffVanishCommand : ScalaCommand()
{
    @CommandAlias("vanish|v|tv|togglevanish")
    @CommandPermission("scstaff.command.vanish")
    @CommandCompletion("0|10|100|1000 @all-players")
    fun onVanish(sender: Player, @Optional priority: Int?, @Optional target: LemonPlayer?)
    {
        if (target != null)
        {
            if (
                !sender.uniqueId.equals(target.bukkitPlayer!!.uniqueId) &&
                !sender.hasPermission("scstaff.command.vanish.other")
            )
            {
                throw ConditionFailedException("You do not have permission to vanish others!")
            }
        }

        val playerToVanish = if (target != null) target.bukkitPlayer!! else sender
        val lemonPlayer = PlayerHandler.findPlayer(playerToVanish).orElse(null)

        val youOrNot = if (target != null) "${CC.YELLOW}${playerToVanish.name}${CC.RED} does" else "You do"
        val youHave = if (target != null) "${coloredName(playerToVanish)}${CC.SEC} has" else "${CC.SEC}You've"

        if (
            !playerToVanish.isOp && lemonPlayer.activeGrant!!.getRank().weight <
            (priority ?: lemonPlayer.activeGrant!!.getRank().weight)
        )
        {
            throw ConditionFailedException("$youOrNot not have permission to vanish with that priority.")
        }

        if (!playerToVanish.hasMetadata("vanished"))
        {
            val power = priority ?: lemonPlayer.activeGrant!!.getRank().weight

            ModerationService.enterVanish(
                playerToVanish, power = power
            )

            sender.sendMessage(
                "${ScalaStaffPlugin.PREFIX} ${CC.SEC}$youHave been vanished with a power of ${CC.PRI}$power${CC.SEC}."
            )

            notify(sender, "vanished")
        } else
        {
            ModerationService.leaveVanish(playerToVanish)

            sender.sendMessage(
                "${ScalaStaffPlugin.PREFIX} ${CC.SEC}$youHave been un-vanished."
            )

            notify(sender, "unvanished")
        }
    }
}
