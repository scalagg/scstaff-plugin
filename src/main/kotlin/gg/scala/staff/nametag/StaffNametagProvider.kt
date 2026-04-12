package gg.scala.staff.nametag

import net.evilblock.cubed.nametag.NametagInfo
import net.evilblock.cubed.nametag.NametagProvider
import net.evilblock.cubed.nametag.NametagProviderRegister
import org.bukkit.ChatColor
import org.bukkit.entity.Player

@NametagProviderRegister
object StaffNametagProvider : NametagProvider(
    "staff", Int.MAX_VALUE - 30000
)
{

    override fun fetchNametag(
        toRefresh: Player, refreshFor: Player
    ): NametagInfo?
    {
        val vanished = toRefresh
            .hasMetadata("vanished")

        if (!vanished)
        {
            return null
        }

        return createNametag(
            "${ChatColor.GRAY}[V] ",
            "", "zzz"
        )
    }
}
