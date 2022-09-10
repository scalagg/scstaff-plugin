package gg.scala.staff

import gg.scala.commons.ExtendedScalaPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

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
class ScalaStaffPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        val 

        const val NAMESPACE = "staff"
        const val STAFF_NODE = "scstaff.staff-member"
    }
}
