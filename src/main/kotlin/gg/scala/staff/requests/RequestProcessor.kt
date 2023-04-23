package gg.scala.staff.requests

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.ScalaStaffConfig
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.commands.requests.RequestCommand
import gg.scala.staff.stream.StaffMessageStream
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 4/23/2023
 */
@Service
object RequestProcessor
{
    val stream = ScalaCommonsSpigot.instance.kvConnection

    @Inject
    lateinit var plugin: ScalaStaffPlugin

    @Configure
    fun configure()
    {
        if (plugin.config<ScalaStaffConfig>().requestsEnabled)
        {
            plugin.commandManager.registerCommand(RequestCommand)
        }
    }

    fun getExpiration(player: Player): CompletableFuture<Long?>
    {
        return CompletableFuture
            .supplyAsync {
                this.stream.sync()
                    .get("requests:expiration:${player.uniqueId}")
            }
            .thenApply {
                it?.toLongOrNull()
            }
            .toCompletableFuture()
    }

    fun processRequest(player: Player, reason: String): CompletableFuture<Void>
    {
        val millis = TimeUnit.SECONDS.toMillis(90L)
        val expiration = System.currentTimeMillis() + millis

        return CompletableFuture
            .runAsync {
                stream.sync()
                    .set(
                        "requests:expiration:${player.uniqueId}",
                        expiration.toString()
                    )
            }
            .thenRun {
                stream.sync().pexpire(
                    "requests:expiration:${player.uniqueId}", millis
                )
            }
            .toCompletableFuture()
            .thenRun {
                StaffMessageStream.send(
                    "${CC.YELLOW}${player.displayName}${CC.RED} has requested: ${CC.WHITE}$reason${CC.RED}.",
                    mustNotHaveMetadata = "reports-disabled"
                )
            }
    }
}
