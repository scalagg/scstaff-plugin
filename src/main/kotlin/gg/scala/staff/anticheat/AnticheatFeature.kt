package gg.scala.staff.anticheat

import dev.sim0n.anticheat.event.AntiCheatAlertEvent
import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import gg.scala.staff.stream.StaffMessageStream
import me.lucko.helper.Events
import me.lucko.helper.utils.Players
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import java.util.UUID

/**
 * @author GrowlyX
 * @since 12/23/2023
 */
@Service
object AnticheatFeature
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("${ScalaStaffPlugin.NAMESPACE}:anticheat-alerts")
            .logger(this.plugin.logger)
            .codec(AwareMessageCodec)
            .build()
    }

    @Configure
    fun configure()
    {
        data class AnticheatAlert(
            val playerID: UUID,
            val checkType: String,
            val label: String,
            val violations: Double
        )

        if (plugin.server.pluginManager.isPluginEnabled("anticheat"))
        {
            Events
                .subscribe(AntiCheatAlertEvent::class.java)
                .handler {
                    AwareMessage.of(
                        "incoming",
                        this.aware,
                        "server" to ServerSync.getLocalGameServer().id,
                        "alert" to AnticheatAlert(
                            playerID = it.player.uniqueId,
                            checkType = it.type.getName(),
                            violations = it.violations,
                            label = it.label
                        )
                    )
                }
                .bindWith(plugin)
        }

        aware.listen("incoming") {
            val serverID = retrieve<String>("server")
            if (serverID == ServerSync.getLocalGameServer().id)
            {
                return@listen
            }

            val alert = retrieve<AnticheatAlert>("alert")
            val fancyMessage = FancyMessage()
                .withMessage(
                    "${CC.RED}[AC] ${CC.D_RED}[$serverID] ${CC.WHITE}${alert.playerID.username()} failed ${CC.B_WHITE}${alert.checkType}${CC.RED}.${
                        " ${CC.GRAY}(VL: ${alert.violations})${
                            if (alert.label.isNotBlank()) " ${CC.GRAY}(${alert.label})" else ""
                        }"
                    }"
                )
                .andHoverOf(
                    "${CC.GREEN}Click to spectate player!"
                )
                .andCommandOf(
                    ClickEvent.Action.RUN_COMMAND,
                    "/spectate ${alert.playerID}"
                )

            Players.all()
                .filter {
                    it.hasPermission("anticheat.alerts") &&
                        it.hasPermission(ScalaStaffPlugin.STAFF_NODE)
                }
                .filter {
                    val profile = StaffProfileService.find(it)
                    profile?.staffGlobalAntiCheatAlerts == true
                }
                .forEach(fancyMessage::sendToPlayer)
        }

        aware.connect().toCompletableFuture().join()
    }
}
