package gg.scala.staff.moderation.hotbar

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.hotbar.HotbarPreset
import gg.scala.lemon.hotbar.HotbarPresetHandler
import gg.scala.lemon.hotbar.entry.impl.StaticHotbarPresetEntry
import gg.scala.staff.network.NetworkOnlineStaffMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder

object ModModeHotbar
{
    val hotbar by lazy {
        val preset = HotbarPreset()

        preset.addSlot(
            0,
            StaticHotbarPresetEntry(
                ItemBuilder(XMaterial.BOOK)
                    .name("${CC.GREEN}Inspect Player ${CC.GRAY}(Right-Click)")
                    .addToLore(
                        "${CC.WHITE}Click a player using",
                        "${CC.WHITE}this book to take a look",
                        "${CC.WHITE}at their inventory.",
                    )
            )
        )

        preset.addSlot(
            1,
            StaticHotbarPresetEntry(
                ItemBuilder(XMaterial.COMPASS)
                    .name("${CC.GREEN}Push Forward ${CC.GRAY}(Right-Click)")
                    .addToLore(
                        "${CC.WHITE}Click this item to",
                        "${CC.WHITE}push yourself forward."
                    )
            ).apply {
                onClick = context@{ player ->
                    player.velocity = player.location
                        .direction.multiply(1.5F)
                }
            }
        )

        preset.addSlot(
            8,
            StaticHotbarPresetEntry(
                ItemBuilder(XMaterial.PAPER)
                    .name("${CC.GREEN}Staff List ${CC.GRAY}(Right-Click)")
                    .addToLore(
                        "${CC.WHITE}View all online staff."
                    )
            ).apply {
                onClick = context@{ player ->
                    NetworkOnlineStaffMenu().openMenu(player)
                }
            }
        )

        HotbarPresetHandler.startTrackingHotbar("mod-mode", preset)

        return@lazy preset
    }
}
