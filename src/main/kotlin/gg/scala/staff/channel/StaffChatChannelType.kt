package gg.scala.staff.channel

import net.kyori.adventure.text.format.NamedTextColor

/**
 * @author GrowlyX
 * @since 4/6/2022
 */
enum class StaffChatChannelType(
    val prefix: Char,
    val color: NamedTextColor
)
{
    Staff('#', NamedTextColor.AQUA),
    Admin('@', NamedTextColor.RED),
    Developer('$', NamedTextColor.BLUE),
    Owner('!', NamedTextColor.DARK_RED)
}
