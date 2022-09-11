package gg.scala.staff.nametag

import net.evilblock.cubed.nametag.NametagInfo
import net.evilblock.cubed.nametag.NametagProvider
import net.evilblock.cubed.nametag.NametagProviderRegister
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@NametagProviderRegister
object StaffNametagProvider : NametagProvider(
    "staff", 1000
)
{
    /**
     * We want to keep vanished/mod-moded players on the bottom of the tablist
     *
     * It's really just for aesthetic purposes as regular players
     * are unable to view the tablist entry anyway.
     *
     * @author GrowlyX
     * @since 9/10/2022
     */
    override fun fetchNametag(
        toRefresh: Player, refreshFor: Player
    ): NametagInfo?
    {
        val vanished = toRefresh.hasMetadata("vanished")
        val modMode = toRefresh.hasMetadata("mod-mode")

        if (!vanished && !modMode)
            return null

        return createNametag(
            "${ChatColor.GRAY}${
                if (modMode) "[M] " else ""
            }${
                if (vanished) "[V] " else ""
            }",
            "", "zzz"
        )
    }
}
