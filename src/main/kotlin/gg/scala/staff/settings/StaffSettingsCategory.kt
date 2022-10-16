package gg.scala.staff.settings

import gg.scala.basics.plugin.conversation.settings.MessagingSettingValue
import gg.scala.basics.plugin.settings.SettingCategory
import gg.scala.basics.plugin.settings.SettingContainer
import gg.scala.basics.plugin.settings.defaults.values.StateSettingValue
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.staff.ScalaStaffPlugin
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/14/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("ScBasics")
object StaffSettingsCategory : SettingCategory
{
    override val items = listOf(
        SettingContainer.buildEntry {
            id = "messages"
            displayName = "Security notifications"

            clazz = StateSettingValue::class.java
            default = StateSettingValue.ENABLED

            description += "Allows you to disable"
            description += "these action notifications:"
            description += ""
            description += "${CC.I_GRAY}[GrowlyX: ${CC.YELLOW}enabled fly${CC.I_GRAY}]"

            displayPredicate = {
                it.hasPermission("scbasics.notifications")
            }

            item = ItemBuilder.of(Material.DIODE)
        },
        SettingContainer.buildEntry {
            id = "messages"
            displayName = "VPN logins"

            clazz = StateSettingValue::class.java
            default = StateSettingValue.ENABLED

            description += "Allows you to disable"
            description += "VPN login alerts."

            item = ItemBuilder.of(Material.ANVIL)
        }
    )

    override fun display(player: Player) =
        player.hasPermission(ScalaStaffPlugin.STAFF_NODE)

    override val displayName = "Staff"
    override val description = listOf(
        "Miscellaneous staff settings.",
        "",
        "${CC.GRAY}Additional settings can be",
        "${CC.GRAY}found in the /staffsettings",
        "${CC.GRAY}menu."
    )
}
