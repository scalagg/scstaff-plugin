package gg.scala.staff.network

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.lemon.player.rank.Rank
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.ScalaStaffPlugin
import me.lucko.helper.promise.ThreadContext
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Maps all online staff on the desired
 * network to their prominent rank.
 *
 * Online staff are refreshed every second.
 *
 * TODO: We may want to cache prominent ranks in
 *  Redis to avoid querying the database this many times
 *
 * @author GrowlyX
 * @since 9/8/2022
 */
@Repeating(40L, context = ThreadContext.ASYNC)
object NetworkOnlineStaffUpdates : Runnable
{
    data class StaffMember(
        val uniqueId: UUID,
        val rankId: UUID,
        val server: String
    )

    val staffMembers = CopyOnWriteArrayList<StaffMember>()

    override fun run()
    {
        val servers = ServerContainer
            .allServers<GameServer>()

        val staffMembers = mutableListOf<StaffMember>()

        for (server in servers)
        {
            for (player in server.getMetadataValue<List<String>>(
                "server", "online-list"
            )!!)
            {
                val uniqueId = UUID.fromString(player)

                val rank = QuickAccess
                    .computeRank(uniqueId).join()
                    ?: continue

                if (isStaffRank(rank))
                {
                    staffMembers += StaffMember(
                        uniqueId, rank.uuid, server.id
                    )
                }
            }
        }

        NetworkOnlineStaffUpdates.staffMembers.clear()
        NetworkOnlineStaffUpdates.staffMembers.addAll(staffMembers)
    }

    private fun isStaffRank(rank: Rank?): Boolean
    {
        return rank
            ?.getCompoundedPermissions()
            ?.contains(ScalaStaffPlugin.STAFF_NODE)
            ?: false
    }
}
