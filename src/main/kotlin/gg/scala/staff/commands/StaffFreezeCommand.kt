package gg.scala.staff.commands

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.Lemon
import gg.scala.lemon.player.LemonPlayer
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.moderation.frozen.FrozenPlayerHandler
import gg.scala.staff.moderation.frozen.event.PostFreezeEvent
import gg.scala.staff.stream.StaffMessageStream
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.command.CommandSender
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object StaffFreezeCommand : ScalaCommand()
{
    @CommandAlias("freeze|fr|ss")
    @CommandCompletion("@players")
    @CommandPermission("scstaff.command.freeze")
    fun onFreeze(sender: CommandSender, target: LemonPlayer)
    {
        val alreadyFrozen = target.bukkitPlayer!!.hasMetadata("frozen")

        if (alreadyFrozen)
        {
            StaffMessageStream.send(
                "${CC.AQUA}${QuickAccess.nameOrConsole(sender)}${CC.D_AQUA} has unfrozen ${QuickAccess.coloredName(target.bukkitPlayer!!)}${CC.D_AQUA}."
            )

            sender.sendMessage("${ScalaStaffPlugin.PREFIX} ${CC.GREEN}You've unfrozen ${QuickAccess.coloredName(target.bukkitPlayer!!)}${CC.GREEN}.")
            target.bukkitPlayer!!.removeMetadata("frozen", Lemon.instance)

            FrozenPlayerHandler.expirables.remove(target.bukkitPlayer!!.uniqueId)
        } else
        {
            StaffMessageStream.send(
                "${CC.AQUA}${QuickAccess.nameOrConsole(sender)}${CC.D_AQUA} has frozen ${QuickAccess.coloredName(target.bukkitPlayer!!)}${CC.D_AQUA}."
            )

            sender.sendMessage("${ScalaStaffPlugin.PREFIX} ${CC.GREEN}You've frozen ${QuickAccess.coloredName(target.bukkitPlayer!!)}${CC.GREEN}.")

            target.bukkitPlayer!!.setMetadata(
                "frozen",
                FixedMetadataValue(
                    Lemon.instance, true
                )
            )

            Tasks.async {
                PostFreezeEvent(target.bukkitPlayer!!).flow().subscribe()
            }

            FrozenPlayerHandler.expirables[target.bukkitPlayer!!.uniqueId] = FrozenPlayerHandler.FrozenExpirable()
        }
    }
}
