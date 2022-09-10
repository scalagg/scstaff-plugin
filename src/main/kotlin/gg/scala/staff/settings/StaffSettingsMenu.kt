package gg.scala.staff.settings

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.player.metadata.Metadata
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
class StaffSettingsMenu : Menu("Staff Settings")
{
    init
    {
        placeholder = true
        updateAfterClick = true
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        val profile = StaffProfileService.find(player)
            ?: return buttons

        buttons[1] = ItemBuilder
            .of(Material.SIGN)
            .name(
                "${
                    if (profile.staffMessagesEnabled) CC.B_GREEN else CC.B_RED
                }Staff Messages"
            )
            .addToLore(
                "${CC.WHITE}Enable or disable staff",
                "${CC.WHITE}messages for your account.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${
                    profile.staffMessagesEnabled
                }",
                "",
                "${CC.GREEN}Click to toggle!"
            )
            .toButton { _, _ ->
                profile.staffMessagesEnabled = !profile.staffMessagesEnabled
                profile.save()
            }

        buttons[3] = ItemBuilder
            .of(Material.EYE_OF_ENDER)
            .name(
                "${
                    if (profile.staffVisibilityEnabled) CC.B_GREEN else CC.B_RED
                }Staff Visibility"
            )
            .addToLore(
                "${CC.WHITE}Enable or disable staff",
                "${CC.WHITE}visibility on your account.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${
                    profile.staffVisibilityEnabled
                }",
                "",
                "${CC.GREEN}Click to toggle!"
            )
            .toButton { _, _ ->
                profile.staffVisibilityEnabled = !profile.staffVisibilityEnabled
                profile.save()
            }

        buttons[5] = ItemBuilder
            .of(Material.DIAMOND_AXE)
            .name(
                "${
                    if (profile.staffAutoVanishEnabled) CC.B_GREEN else CC.B_RED
                }Auto Vanish"
            )
            .addToLore(
                "${CC.WHITE}Enable or disable auto",
                "${CC.WHITE}vanish on your account.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${
                    profile.staffAutoVanishEnabled
                }",
                "",
                "${CC.GREEN}Click to toggle!"
            )
            .toButton { _, _ ->
                profile.staffAutoVanishEnabled = !profile.staffAutoVanishEnabled
                profile.save()
            }

        val lemonProfile = PlayerHandler.find(player.uniqueId)
            ?: return buttons

        buttons[7] = ItemBuilder
            .of(Material.PAPER)
            .name(
                "${
                    if (lemonProfile.hasMetadata("filter-disabled")) CC.B_GREEN else CC.B_RED
                }Filtered Messages"
            )
            .addToLore(
                "${CC.WHITE}Enable or disable receival",
                "${CC.WHITE}of filtered messages.",
                "",
                "${CC.WHITE}Current: ${CC.YELLOW}${
                    lemonProfile.hasMetadata("filter-disabled")
                }",
                "",
                "${CC.GREEN}Click to toggle!"
            )
            .toButton { _, _ ->
                if (lemonProfile.hasMetadata("filter-disabled"))
                {
                    lemonProfile.removeMetadata("filter-disabled")
                } else
                {
                    lemonProfile.updateOrAddMetadata("filter-disabled", Metadata(true))
                }

                lemonProfile.save()
            }

        return buttons
    }
}
