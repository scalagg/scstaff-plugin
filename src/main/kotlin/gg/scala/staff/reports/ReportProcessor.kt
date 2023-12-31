package gg.scala.staff.reports

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Sorts
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.reports.model.Report
import gg.scala.staff.stream.StaffMessageStream
import gg.scala.store.controller.DataStoreObjectControllerCache
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

    @Configure
    fun configure()
    {
        DataStoreObjectControllerCache
            .findNotNull<Report>()
            .mongo()
            .connection
            .getAppliedResource()
            .getCollection("Report")
            .createIndex(
                Sorts.ascending("timestamp"),
                IndexOptions()
                    .expireAfter(130, TimeUnit.SECONDS)
            )
    }

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
        player: Player, target: UUID, type: ReportType, reason: String? = null
    ): CompletableFuture<Void>
    {
        val millis = TimeUnit.SECONDS.toMillis(90L)
        val expiration = System.currentTimeMillis() + millis

        return CompletableFuture
            .runAsync {
                stream.sync()
                    .psetex(
                        "reports:expiration:${player.uniqueId}",
                        millis,
                        expiration.toString()
                    )
            }
            .thenRun {
                stream.sync().incr(
                    "reports:expiration:${player.uniqueId}"
                )
            }
            .toCompletableFuture()
            .thenRun {
                StaffMessageStream.send(
                    "${CC.YELLOW}${
                        QuickAccess.computeColoredName(player.uniqueId, player.name).join()
                    }${CC.RED} has reported ${CC.YELLOW}${
                        QuickAccess.computeColoredName(target, target.username()).join()
                    }${CC.RED} for ${CC.GOLD}${type.fancyName}${CC.RED}${
                        if (reason != null) " ${CC.GRAY}($reason)${CC.RED}" else ""
                    }.",
                    mustNotHaveMetadata = "reports-disabled"
                )
            }
    }
}
