package gg.scala.staff.notes

import gg.scala.lemon.util.SplitUtil
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*

data class Note(
    val uniqueId: UUID,
    val issuer: UUID,
    val target: UUID,
    val timestamp: Date,
    var value: String
) : IDataStoreObject
{
    override val identifier: UUID
        get() = uniqueId

    internal val shortenedUniqueId = SplitUtil.splitUuid(uniqueId)
}
