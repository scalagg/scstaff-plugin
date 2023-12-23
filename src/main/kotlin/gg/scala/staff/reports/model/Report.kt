package gg.scala.staff.reports.model

import gg.scala.commons.annotations.Model
import gg.scala.store.controller.annotations.Indexed
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.UUID

/**
 * @author GrowlyX
 * @since 11/23/2023
 */
@Model
data class Report(
    override val identifier: UUID,
    @Indexed
    val from: UUID,
    @Indexed
    val to: UUID,
    val reason: String,
    val timestamp: Long = System
        .currentTimeMillis()
) : IDataStoreObject
