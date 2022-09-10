package gg.scala.staff

import gg.scala.lemon.handler.PlayerHandler
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
fun notify(
    sender: CommandSender, message: String
)
{
    val senderName = if (sender is Player) sender.name else "Console"

    for (onlinePlayer in Bukkit.getOnlinePlayers())
    {
        val lemonPlayer = PlayerHandler
            .find(onlinePlayer.uniqueId)
            ?: continue

        if (lemonPlayer.hasMetadata("security-notifications"))
            continue

        if (onlinePlayer.hasPermission("lemon.security.notifications"))
        {
            onlinePlayer.sendMessage(
                "${CC.I_GRAY}[$senderName: ${CC.YELLOW}$message${CC.I_GRAY}]"
            )
        }
    }
}
