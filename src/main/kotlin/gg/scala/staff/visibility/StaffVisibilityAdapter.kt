package gg.scala.staff.visibility

import gg.scala.lemon.handler.PlayerHandler
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import net.evilblock.cubed.visibility.VisibilityAction
import net.evilblock.cubed.visibility.VisibilityAdapter
import net.evilblock.cubed.visibility.VisibilityAdapterRegister
import org.bukkit.entity.Player

@VisibilityAdapterRegister("staff")
object StaffVisibilityAdapter : VisibilityAdapter
{
    override fun getAction(
        toRefresh: Player, refreshFor: Player
    ): VisibilityAction
    {
        if (toRefresh.uniqueId == refreshFor.uniqueId)
        {
            return VisibilityAction.NEUTRAL
        }

        val lemonPlayer = PlayerHandler
            .find(refreshFor.uniqueId)
            ?: return VisibilityAction.NEUTRAL

        val profilePlayer = StaffProfileService
            .find(refreshFor)
            ?: return VisibilityAction.NEUTRAL

        val lemonPlayerTarget = PlayerHandler
            .find(toRefresh.uniqueId)
            ?: return VisibilityAction.NEUTRAL

        if (
            !profilePlayer.staffVisibilityEnabled &&
            lemonPlayer.hasPermission(ScalaStaffPlugin.STAFF_NODE) &&
            lemonPlayerTarget.hasPermission(ScalaStaffPlugin.STAFF_NODE)
        )
        {
            return VisibilityAction.HIDE
        }

        if (
            toRefresh.hasMetadata("vanished") &&
            (lemonPlayer.activeGrant?.getRank()?.weight ?: 0) <
            toRefresh.getMetadata("vanish-power")[0].asInt()
        )
        {
            return VisibilityAction.HIDE
        }

        if (
            !profilePlayer.vanishVisibilityEnabled &&
            toRefresh.hasMetadata("vanished") &&
            lemonPlayer.hasPermission(ScalaStaffPlugin.STAFF_NODE)
        )
        {
            return VisibilityAction.HIDE
        }

        return VisibilityAction.NEUTRAL
    }
}
