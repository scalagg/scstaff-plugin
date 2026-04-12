package gg.scala.staff.channel

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor

enum class StaffChatChannelType(
    val prefix: Char,
    val color: NamedTextColor,
    val chatColor: ChatColor,
)
{
    STAFF('#', NamedTextColor.AQUA, ChatColor.AQUA),
    ADMIN('@', NamedTextColor.RED, ChatColor.RED),
    ENGINEER('$', NamedTextColor.GOLD, ChatColor.GOLD),
    OWNER('!', NamedTextColor.DARK_RED, ChatColor.DARK_RED)
}
