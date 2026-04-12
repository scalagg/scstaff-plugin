package gg.scala.staff.moderation.chat

import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.staff.ScalaStaffPlugin

@Service
object ChatControlSyncService : DataSyncService<ChatControlSyncConfig>()
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = ChatControlSyncKeys
    override fun type() = ChatControlSyncConfig::class.java
}
