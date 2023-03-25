package gg.scala.staff.moderation.chat

import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.staff.ScalaStaffPlugin

/**
 * @author GrowlyX
 * @since 10/16/2022
 */
@Service
object ChatControlSyncService : DataSyncService<ChatControlSyncConfig>()
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    override fun keys() = ChatControlSyncKeys
    override fun type() = ChatControlSyncConfig::class.java
}
