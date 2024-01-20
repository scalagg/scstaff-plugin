package gg.scala.staff.anticheat

import dev.sim0n.anticheat.check.CheckType
import dev.sim0n.anticheat.event.AntiCheatAlertEvent
import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.anticheat.sampling.SixtySecondSample
import gg.scala.staff.anticheat.sampling.TenSecondSample
import gg.scala.staff.profile.StaffProfileService
import gg.scala.staff.stream.StaffMessageStream
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import me.lucko.helper.utils.Players
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.TimeUnit

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

    data class AbstractAnticheatAlert(
        val player: Player,
        val type: AnticheatCheck,
        val label: String,
        val violations: Double
    )

    private var alertFilter: (AbstractAnticheatAlert) -> Boolean = { true }
    fun configureAlertFilter(block: (AbstractAnticheatAlert) -> Boolean)
    {
        this.alertFilter = block
    }

    fun subscribeToSixtySecondSampleOf(
        check: AnticheatCheck, player: Player, evaluate: (SixtySecondSample) -> Unit
    ): CompositeTerminable
    {
        val terminable = CompositeTerminable.create()
        val sixtySecondSample = SixtySecondSample()

        var seconds = 0
        Schedulers
            .async()
            .runRepeating({ _ ->
                if (seconds == 60)
                {
                    evaluate(sixtySecondSample)
                    terminable.closeAndReportException()
                    return@runRepeating
                }

                if (seconds % 10 == 0)
                {
                    sixtySecondSample.addToSampleAndReset()
                    seconds += 1
                    return@runRepeating
                }

                sixtySecondSample.useCurrentSample(
                    TenSecondSample::resetAndAddToSample
                )
                seconds += 1
            }, 0L, TimeUnit.SECONDS, 1L, TimeUnit.SECONDS)
            .bindWith(terminable)

        Events
            .subscribe(
                AntiCheatAlertEvent::class.java,
                EventPriority.HIGHEST
            )
            .filter {
                it.player.uniqueId == player.uniqueId
            }
            .handler {
                sixtySecondSample.useCurrentSample(TenSecondSample::triggerAlert)
            }
            .bindWith(terminable)

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .filter { it.player.uniqueId == player.uniqueId  }
            .handler {
                terminable.closeAndReportException()
            }
            .bindWith(terminable)

        return terminable
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
            fun Player.runAutoBanFor(reason: String)
            {
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "ban $name 30d Unfair Advantage ($reason) -s"
                )
            }

            Events
                .subscribe(AntiCheatAlertEvent::class.java, EventPriority.MONITOR)
                .handler {
                    if (alertFilter(
                            AbstractAnticheatAlert(
                                player = it.player,
                                type = AnticheatCheck.valueOf(it.type.name),
                                label = it.label,
                                violations = it.violations
                            )
                        ))
                    {
                        it.isCancelled = true
                        return@handler
                    }

                    AwareMessage
                        .of(
                            "incoming",
                            this.aware,
                            "server" to ServerSync.getLocalGameServer().id,
                            "alert" to Serializers.gson.toJson(
                                AnticheatAlert(
                                    playerID = it.player.uniqueId,
                                    checkType = it.type.getName(),
                                    violations = it.violations,
                                    label = it.label
                                )
                            )
                        )
                        .publish(AwareThreadContext.ASYNC)

                    when (true)
                    {
                        (it.type == CheckType.TIMER && it.violations >= 15.0) ->
                            it.player.runAutoBanFor("ETV")
                        (it.type == CheckType.REACH && it.violations >= 10.0) ->
                            it.player.runAutoBanFor("ERV")
                        else -> {}
                    }
                }
                .bindWith(plugin)
        }

        aware.listen("incoming") {
            val serverID = retrieve<String>("server")
            if (serverID == ServerSync.getLocalGameServer().id)
            {
                return@listen
            }

            val alert = Serializers.gson.fromJson(
                retrieve<String>("alert"),
                AnticheatAlert::class.java
            )

            val fancyMessage = FancyMessage()
                .withMessage(
                    "${CC.RED}[AC] ${CC.D_RED}[$serverID] ${CC.WHITE}${alert.playerID.username()}${CC.RED} failed ${CC.B_WHITE}${alert.checkType}${
                        if (alert.label.isNotBlank()) " ${alert.label}" else ""
                    }${CC.RED}.${
                        " ${CC.GRAY}(VL: ${alert.violations})"
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
