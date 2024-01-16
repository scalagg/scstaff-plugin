package gg.scala.staff.reports.model

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.Model
import gg.scala.staff.reports.ReportType
import gg.scala.store.controller.annotations.Indexed
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.UUID

/**
 * @author GrowlyX
 * @since 11/23/2023
 */
@Model
data class PlayerReport(
    override val identifier: UUID,
    @Indexed
    val from: UUID,
    @Indexed
    val to: UUID,
    val category: ReportType,
    val reason: String,
    val serverProvidedInformation: String? = null,
    val relatedAccounts: Set<UUID>,
    @Indexed
    val sentFrom: String = ServerSync.getLocalGameServer().id,
    @Indexed
    val priority: Priority = Priority.Normal,
    val timestamp: Long = System
        .currentTimeMillis()
) : IDataStoreObject
