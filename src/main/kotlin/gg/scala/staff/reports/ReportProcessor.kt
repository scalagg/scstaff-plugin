package gg.scala.staff.reports

import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Sorts
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.reports.model.Priority
import gg.scala.staff.reports.model.PlayerReport
import gg.scala.staff.stream.StaffMessageStream
import gg.scala.store.controller.DataStoreObjectControllerCache
import io.lettuce.core.api.StatefulRedisConnection
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.CC
import org.bson.conversions.Bson
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service
object ReportProcessor
{
    private val stream: StatefulRedisConnection<String, String> =
        ScalaCommonsSpigot.instance.kvConnection

    private var additionalServerDataProviders = mutableListOf<(Player, UUID) -> Map<String, String>>()
    fun provideAdditionalServerData(
        contextProvider: (Player, UUID) -> Map<String, String>
    )
    {
        additionalServerDataProviders += contextProvider
    }

    private var reportTypePreChecks = mutableListOf<(Player, ReportType) -> Boolean>()
    fun configureReportTypePreChecks(
        isAllowed: (Player, ReportType) -> Boolean
    )
    {
        reportTypePreChecks += isAllowed
    }

    @Configure
    fun configure()
    {
        val collection = DataStoreObjectControllerCache
            .findNotNull<PlayerReport>()
            .mongo()
            .connection
            .getAppliedResource()
            .getCollection("PlayerReport")

        collection.createIndex(
            Sorts.ascending("timestamp"),
            IndexOptions()
                .expireAfter(5, TimeUnit.MINUTES)
        )
    }

    private fun aggregateReports(filters: Bson) = DataStoreObjectControllerCache
        .findNotNull<PlayerReport>()
        .mongo()
        .loadAllWithFilter(filters)
        .thenApply { it.values.toList() }

    fun getAllReportsOnUserFrom(user: UUID, from: UUID) = aggregateReports(
        Filters.and(
            Filters.eq("to", user.toString()),
            Filters.eq("from", from.toString())
        )
    )

    fun getAllReportsOnUser(user: UUID) = aggregateReports(
        Filters.eq("to", user.toString())
    )

    fun getAllPrioritizedReportsOnUser(
        user: UUID, priority: Priority = Priority.High
    ) = aggregateReports(
        Filters.and(
            Filters.eq("to", user.toString()),
            Filters.eq("priority", priority.toString())
        )
    )

    fun getAllCategorizedReportsOnUser(
        user: UUID, category: ReportType
    ) = aggregateReports(
        Filters.and(
            Filters.eq("to", user.toString()),
            Filters.eq("category", category.toString())
        )
    )

    fun getAllCategorizedReportsOnUserFrom(
        user: UUID, from: UUID, category: ReportType
    ) = aggregateReports(
        Filters.and(
            Filters.eq("to", user.toString()),
            Filters.eq("from", from.toString()),
            Filters.eq("category", category.toString())
        )
    )

    fun getAllCategorizedAndPrioritizedReportsOnUser(
        user: UUID, category: ReportType, priority: Priority = Priority.High
    ) = aggregateReports(
        Filters.and(
            Filters.eq("to", user.toString()),
            Filters.eq("category", category.toString()),
            Filters.eq("priority", priority.toString())
        )
    )

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
