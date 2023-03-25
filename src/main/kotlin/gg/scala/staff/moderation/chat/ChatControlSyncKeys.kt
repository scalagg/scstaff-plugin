package gg.scala.staff.moderation.chat

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.persist.datasync.DataSyncKeys

/**
 * @author GrowlyX
 * @since 10/12/2022
 */
object ChatControlSyncKeys : DataSyncKeys
{
    private val NAMESPACE = "staff:chatcontrol:groups:${ServerSync.getLocalGameServer().groups.first()}"

    override fun store() = keyOf(NAMESPACE, "configuration")
    override fun sync() = keyOf(NAMESPACE, "update")
}
