package gg.scala.staff.reports.model

import gg.scala.common.Savable
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.Model
import gg.scala.staff.reports.ReportType
import gg.scala.staff.reports.model.wrapper.WBsonDate
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.controller.annotations.Indexed
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

/**
 * @author GrowlyX
 * @since 11/23/2023
 */
@Model
data class PlayerReport(
    @Indexed
    val from: UUID,
    @Indexed
    val to: UUID,
    val category: ReportType,
    val playerProvidedContext: String? = null,
    val serverProvidedContext: String? = null,
    val relatedAccounts: Set<UUID>,
    override val identifier: UUID = UUID.randomUUID(),
    @Indexed
    val fromServer: String = ServerSync.getLocalGameServer().id,
    @Indexed
    var priority: Priority = Priority.Normal,
    val timestamp: WBsonDate = WBsonDate(System.currentTimeMillis())
) : IDataStoreObject, Savable
{
    override fun save() = DataStoreObjectControllerCache
        .findNotNull<PlayerReport>()
        .save(this, DataStoreStorageType.MONGO)
}
