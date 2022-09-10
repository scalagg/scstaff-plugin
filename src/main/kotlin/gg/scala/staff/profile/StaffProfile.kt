package gg.scala.staff.profile

import gg.scala.common.Savable
import gg.scala.staff.channel.StaffChatChannelType
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
data class StaffProfile(
    override val identifier: UUID
) : IDataStoreObject, Savable
{
    var staffChannel: StaffChatChannelType? = null

    var staffMessagesEnabled = true
    var staffVisibilityEnabled = true

    override fun save() =
        DataStoreObjectControllerCache
            .findNotNull<StaffProfile>()
            .save(this, DataStoreStorageType.MONGO)
}
