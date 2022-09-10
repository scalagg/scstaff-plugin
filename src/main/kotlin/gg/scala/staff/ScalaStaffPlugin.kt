package gg.scala.staff

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Plugin(
    name = "ScStaff",
    depends = [
        PluginDependency("Lemon"),
        PluginDependency("scala-commons"),
        PluginDependency("cloudsync", soft = true)
    ]
)
@ContainerConfig("config", ScalaStaffConfig::class)
class ScalaStaffPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        val PREFIX by lazy {
            "${CC.B_PRI}Staff ${ChatColor.GRAY}${ChatColor.BOLD}${Constants.DOUBLE_ARROW_RIGHT}"
        }

        @JvmStatic
        val PREFIX_PLUGIN by lazy {
            "${ChatColor.GOLD}${ChatColor.BOLD}ScStaff ${ChatColor.GRAY}${ChatColor.BOLD}${Constants.DOUBLE_ARROW_RIGHT}"
        }

        const val NAMESPACE = "staff"
        const val STAFF_NODE = "scstaff.staff-member"
    }
}
