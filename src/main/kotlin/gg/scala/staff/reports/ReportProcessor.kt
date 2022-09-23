package gg.scala.staff.reports

import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.stream.StaffMessageStream
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
    val stream = StaffMessageStream.aware.internal().connect()!!

    fun getExpiration(player: Player): CompletableFuture<Long?>
    {
        return this.stream.async()
            .get("reports:expiration:${player.uniqueId}")
            .thenApply {
                it?.toLongOrNull()
            }
            .toCompletableFuture()
    }

    fun processReport(
        player: Player, target: UUID, reason: String
    ): CompletableFuture<Void>
    {
        return stream.async()
            .set(
                "reports:expiration:${player.uniqueId}",
                System.currentTimeMillis().toString()
            )
            .thenCompose {
                stream.async().pexpire(
                    "reports:expiration:${player.uniqueId}",
                    TimeUnit.SECONDS.toMillis(90L)
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
