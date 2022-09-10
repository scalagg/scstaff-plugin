package gg.scala.staff.channel

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 4/6/2022
 */
enum class StaffChatChannelType(
    val prefix: Char,
    val color: NamedTextColor,
    val chatColor: ChatColor,
)
{
    Staff('#', NamedTextColor.AQUA, ChatColor.AQUA),
    Admin('@', NamedTextColor.RED, ChatColor.RED),
    Developer('$', NamedTextColor.BLUE, ChatColor.BLUE),
    Owner('!', NamedTextColor.DARK_RED, ChatColor.DARK_RED)
}
