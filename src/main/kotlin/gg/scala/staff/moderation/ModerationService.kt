package gg.scala.staff.moderation

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.event.EventQuadState
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.menu.modmode.InspectionMenu
import gg.scala.lemon.player.LemonPlayer
import gg.scala.lemon.util.ClientUtil
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.moderation.frozen.event.ModModeEnterEvent
import gg.scala.staff.moderation.hotbar.ModModeHotbar
import gg.scala.staff.moderation.scoreboard.ModModeScoreboardOverride
import gg.scala.staff.profile.StaffProfile
import gg.scala.staff.profile.StaffProfileService
import gg.scala.staff.stream.StaffMessageStream
import me.lucko.helper.Events
import net.evilblock.cubed.nametag.NametagHandler
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.player.PlayerSnapshot
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

    private fun displayStaffInfo(player: Player, profile: StaffProfile, lemonPlayer: LemonPlayer)
    {
        player.sendMessage(arrayOf(
            "${CC.B_PRI}Staff Information:",
            "${ScalaStaffPlugin.PREFIX} ${CC.SEC}Channel: ${
                if (profile.staffChannel == null) "${CC.RED}None" else "${profile.staffChannel!!.chatColor}${profile.staffChannel!!.name}"
            }",
            "${ScalaStaffPlugin.PREFIX} ${CC.SEC}Messages: ${
                if (profile.staffMessagesEnabled) "${CC.GREEN}Shown" else "${CC.RED}Hidden"
            } ${CC.GRAY}(/tsm)",
            "${ScalaStaffPlugin.PREFIX} ${CC.SEC}Reports: ${
                if (profile.staffReportNotificationsEnabled) "${CC.GREEN}Shown" else "${CC.RED}Hidden"
            } ${CC.GRAY}(/trm)",
            "${ScalaStaffPlugin.PREFIX} ${CC.SEC}Filter: ${
                if (!lemonPlayer.hasMetadata("filter-disabled")) "${CC.GREEN}Shown" else "${CC.RED}Hidden"
            } ${CC.GRAY}(/tfm)",
        ))
    }

    val connection = StaffMessageStream
        .aware.internal().connect()!!

    @Configure
    fun configure()
    {
        ScoreboardHandler.scoreboardOverride = ModModeScoreboardOverride

        val scheduler = Executors.newSingleThreadScheduledExecutor()

        val interaction: (Player, Cancellable, String) -> Unit = { player, event, action ->
            if (player.hasMetadata("mod-mode"))
            {
                player.sendMessage("${CC.RED}You cannot $action while in mod-mode.")
                event.isCancelled = true
            } else if (player.hasMetadata("vanished"))
            {
                player.sendMessage("${CC.RED}You cannot $action while vanished.")
                event.isCancelled = true
            }
        }

        Events.subscribe(EntityTargetEvent::class.java)
            .handler { event ->
                if (event.reason != EntityTargetEvent.TargetReason.CUSTOM)
                {
                    val entity = event.entity
                    val target = event.target

                    if (
                        (entity is ExperienceOrb || entity is LivingEntity) &&
                        target is Player && target.hasMetadata("vanished")
                    )
                    {
                        event.isCancelled = true
                    }
                }
            }
            .bindWith(plugin)

        Events.subscribe(BlockBreakEvent::class.java)
            .handler { event ->
                interaction.invoke(event.player, event, "break blocks")
            }
            .bindWith(plugin)

        Events
            .subscribe(PlayerDropItemEvent::class.java)
            .filter {
                it.player.hasMetadata("mod-mode")
            }
            .handler { event ->
                event.isCancelled = true
            }
            .bindWith(plugin)

        Events.subscribe(BlockPlaceEvent::class.java)
            .handler { event ->
                interaction.invoke(event.player, event, "place blocks")
            }
            .bindWith(plugin)

        Events.subscribe(EntityDamageByEntityEvent::class.java)
            .handler { event ->
                val damager = event.damager

                if (damager is Player)
                {
                    interaction.invoke(
                        damager, event, "hurt entities"
                    )
                }
            }
            .bindWith(plugin)

        Events
            .subscribe(
                PlayerJoinEvent::class.java,
                EventPriority.LOWEST
            )
            .handler {
                val lemonPlayer = PlayerHandler
                    .find(it.player.uniqueId)
                    ?: return@handler

                val profile = StaffProfileService
                    .find(it.player)
                    ?: return@handler

                if (it.player.hasPermission(ScalaStaffPlugin.STAFF_NODE))
                {
                    if ("hub" in ServerSync.getLocalGameServer().groups)
                    {
                        scheduler.schedule({
                            displayStaffInfo(it.player, profile, lemonPlayer)
                        },25L, TimeUnit.MILLISECONDS)
                    }

                    if (
                        profile.staffAutoVanishEnabled &&
                        it.player.hasPermission("scstaff.command.autovanish")
                    )
                    {
                        it.player.sendMessage(
                            "${CC.B_SEC}You've been automatically put into vanish."
                        )

                        lemonPlayer.activeGrant?.apply {
                            enterVanish(it.player, this.getRank().weight)
                        }
                    }
                }

                if (it.player.hasPermission("scstaff.client.staffmodules"))
                {
                    ClientUtil.handleApplicableClient(it.player) { client ->
                        client.enableStaffModules(it.player)

                        it.player.sendMessage(
                            "${CC.GREEN}${client.getClientName()} staff modules have been enabled."
                        )
                    }
                }
            }
            .bindWith(plugin)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                leaveModMode(it.player, silent = true)
                leaveVanish(it.player, update = false)
            }
            .bindWith(plugin)

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

    fun enterVanish(player: Player, power: Int = 0)
    {
        player.setMetadata("vanished", FixedMetadataValue(plugin, true))
        player.setMetadata("vanish-power", FixedMetadataValue(plugin, power))

        VisibilityHandler.updateToAll(player)
        NametagHandler.reloadPlayer(player)
    }

    fun leaveVanish(player: Player, update: Boolean = true)
    {
        player.removeMetadata("vanished", plugin)
        player.removeMetadata("vanish-power", plugin)

        if (update)
        {
            VisibilityHandler.updateToAll(player)
            NametagHandler.reloadPlayer(player)
        }
    }

    fun enterModMode(player: Player)
    {
        val enter = ModModeEnterEvent(player)

        enter.flow().subscribe {
            if (it != EventQuadState.CANCELLED)
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
        }
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
