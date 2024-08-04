package gg.scala.staff.anticheat

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.anticheat.sampling.SixtySecondSample
import gg.scala.staff.anticheat.sampling.TenSecondSample
import gg.scala.staff.profile.StaffProfileService
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import me.lucko.helper.utils.Players
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
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
        val type: String,
        val violations: Double,
        val extraInfo: String
    )

    var proxiedPlayerAllowAC = { _: Player -> true }

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
            .subscribe(PlayerQuitEvent::class.java)
            .filter { it.player.uniqueId == player.uniqueId  }
            .handler {
                terminable.closeAndReportException()
            }
            .bindWith(terminable)

        return terminable
    }

    fun onAnticheatAlert(alert: AbstractAnticheatAlert)
    {
        AwareMessage
            .of(
                "incoming",
                this.aware,
                "server" to ServerSync.getLocalGameServer().id,
                "alert" to Serializers.gson.toJson(AnticheatAlert(
                    alert.player.uniqueId, alert.type, alert.violations, alert.extraInfo
                ))
            )
            .publish(AwareThreadContext.ASYNC)
    }

    data class AnticheatAlert
    @JvmOverloads constructor(
        val playerID: UUID,
        val checkType: String,
        val violations: Double,
        val extraInfo: String? = null
    )

    @Configure
    fun configure()
    {
        aware.listen("incoming") {
            val serverID = retrieve<String>("server")
            val alert = Serializers.gson.fromJson(
                retrieve<String>("alert"),
                AnticheatAlert::class.java
            )

            val fancyMessage = FancyMessage()
                .withMessage(
                    "${CC.RED}[AC] ${CC.D_RED}[$serverID] ${CC.WHITE}${alert.playerID.username()}${CC.RED} failed ${CC.B_WHITE}${alert.checkType}${CC.RED}.${
                        " ${CC.GRAY}(VL: ${alert.violations})"
                    }"
                )
                .apply {
                    if (alert.extraInfo != null)
                    {
                        andHoverOf(
                            alert.extraInfo,
                            "",
                            "${CC.GREEN}Click to spectate player!"
                        )
                    } else
                    {
                        andHoverOf(
                            "${CC.GREEN}Click to spectate player!"
                        )
                    }
                }
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
