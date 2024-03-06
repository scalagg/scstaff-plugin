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
    private val chatControlLocalID by lazy {
        if (Lemon.instance.settings.defaultChatGsd)
            Lemon.instance.settings.defaultChatGsdGroupId else
            ServerSync.local.id
    }

    override fun store() = keyOf("asd", "configuration")
    override fun newStore() = "ccontrol:$chatControlLocalID"
    override fun sync() = keyOf(chatControlLocalID, "ccontrol")
}
