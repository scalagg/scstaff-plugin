package gg.scala.staff.moderation.chat

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.lemon.Lemon

/**
 * @author GrowlyX
 * @since 10/12/2022
 */
object ChatControlSyncKeys : DataSyncKeys
{
    override fun store() = keyOf("asd", "configuration")
    override fun newStore() = "ccontrol:${
        if (Lemon.instance.settings.defaultChatGsd) 
            Lemon.instance.settings.defaultChatGsdGroupId else
                ServerSync.local.id
    }"
    override fun sync() = keyOf(ServerSync.getLocalGameServer().groups.first(), "ccontrol")
}
