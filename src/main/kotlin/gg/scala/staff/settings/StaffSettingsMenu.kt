package gg.scala.staff.settings

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

        buttons[3] = ItemBuilder
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

        buttons[5] = ItemBuilder
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

        return buttons
    }
}
