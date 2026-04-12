package gg.scala.staff.profile

import gg.scala.common.Savable
import gg.scala.staff.channel.StaffChatChannelType
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

data class StaffProfile(
    override val identifier: UUID
) : IDataStoreObject, Savable
{
    var staffChannel: StaffChatChannelType? = null

    var staffReportNotificationsEnabled = true
    var staffMessagesEnabled = true

    var staffVisibilityEnabled = true
    var vanishVisibilityEnabled = true

    var staffAutoVanishEnabled = false
    var staffGlobalAntiCheatAlerts: Boolean? = false

    override fun save() =
        DataStoreObjectControllerCache
            .findNotNull<StaffProfile>()
            .save(
                this, DataStoreStorageType.MONGO
            )
}
