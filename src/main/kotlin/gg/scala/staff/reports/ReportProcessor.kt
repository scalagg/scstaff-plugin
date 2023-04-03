package gg.scala.staff.reports

import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.stream.StaffMessageStream
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service // eagerly load our redis stream
object ReportProcessor
{
    val stream = ScalaCommonsSpigot.instance.kvConnection

    fun getExpiration(player: Player): CompletableFuture<Long?>
    {
        return CompletableFuture
            .supplyAsync {
                this.stream.sync()
                    .get("reports:expiration:${player.uniqueId}")
            }
            .thenApply {
                it?.toLongOrNull()
            }
            .toCompletableFuture()
    }

    fun processReport(
        player: Player, target: UUID, reason: String
    ): CompletableFuture<Void>
    {
        val millis = TimeUnit.SECONDS.toMillis(90L)
        val expiration = System.currentTimeMillis() + millis

        return CompletableFuture
            .runAsync {
                stream.sync()
                    .set(
                        "reports:expiration:${player.uniqueId}",
                        expiration.toString()
                    )
            }
            .thenRun {
                stream.sync().pexpire(
                    "reports:expiration:${player.uniqueId}", millis
                )
            }
            .toCompletableFuture()
            .thenRun {
                StaffMessageStream.send(
                    "${CC.YELLOW}${player.displayName}${CC.RED} has reported ${target.username()} for ${CC.GOLD}$reason${CC.RED}.",
                    mustNotHaveMetadata = "reports-disabled"
                )
            }
    }
}
