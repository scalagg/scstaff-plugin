package gg.scala.staff.moderation

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.menu.modmode.InspectionMenu
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.moderation.hotbar.ModModeHotbar
import gg.scala.staff.moderation.scoreboard.ModModeScoreboardOverride
import me.lucko.helper.Events
import net.evilblock.cubed.nametag.NametagHandler
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.player.PlayerSnapshot
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.UUID

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service
object ModerationService
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    private val snapshots =
        mutableMapOf<UUID, PlayerSnapshot>()

    @Configure
    fun configure()
    {
        ScoreboardHandler.scoreboardOverride =
            ModModeScoreboardOverride

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                leaveModMode(it.player, silent = true)
            }

        Events.subscribe(PlayerInteractAtEntityEvent::class.java)
            .filter { it.rightClicked is Player }
            .filter { it.player.inventory.itemInHand != null }
            .filter { it.player.hasMetadata("mod-mode") }
            .handler {
                val rightClicked = it.rightClicked as Player

                val isHoldingViewInventory = it.player.inventory
                    .itemInHand.isSimilar(
                        ModModeHotbar.hotbar.entries[0]
                            ?.finalizedItemStack(it.player)
                    )

                if (isHoldingViewInventory)
                {
                    InspectionMenu(rightClicked).openMenu(it.player)
                }
            }
            .bindWith(plugin)
    }

    fun enterModMode(player: Player)
    {
        snapshots[player.uniqueId] = PlayerSnapshot(player)
        player.inventory.clear()

        player.setMetadata(
            "mod-mode", FixedMetadataValue(plugin, true)
        )

        ModModeHotbar.hotbar.applyToPlayer(player)
        NametagHandler.reloadPlayer(player)

        player.sendMessage(
            "${ScalaStaffPlugin.PREFIX} ${CC.SEC}You are now in mod mode."
        )
    }

    fun leaveModMode(player: Player, silent: Boolean = false)
    {
        player.removeMetadata("mod-mode", plugin)

        snapshots.remove(player.uniqueId)
            ?.restore(player, teleport = false)

        NametagHandler.reloadPlayer(player)

        if (!silent)
        {
            player.sendMessage(
                "${ScalaStaffPlugin.PREFIX} ${CC.SEC}You are no longer in mod mode."
            )
        }
    }
}
