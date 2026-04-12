package gg.scala.staff.channel

import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.lemon.channel.ChatChannelComposite
import gg.scala.lemon.player.rank.Rank
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class StaffChatChannelComposite(
    private val type: StaffChatChannelType
): ChatChannelComposite
{
    private val urlExtractingDeserializer = LegacyComponentSerializer
        .legacySection()
        .toBuilder()
        .extractUrls()
        .build()

    override fun identifier() = type.name.lowercase()

    override fun format(
        sender: UUID, receiver: Player?,
        message: String, server: String, rank: Rank
    ): TextComponent
    {
        val username = ScalaStoreUuidCache.username(sender)

        return Component
            .text("[${type.name}] ")
            .color(type.color)
            .append(
                Component.text("($server) ")
                    .color(NamedTextColor.GRAY)
            )
            .append(
                urlExtractingDeserializer
                    .deserialize(
                        "${rank.prefix} ${rank.color}$username${ChatColor.WHITE}: ${message}"
                    )
            )
    }
}
