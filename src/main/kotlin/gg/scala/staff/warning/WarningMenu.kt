package gg.scala.staff.warning

import gg.scala.lemon.util.QuickAccess.username
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
class WarningMenu(
    private val target: UUID
) : PaginatedMenu()
{
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        for (warning in WarningHandler
            .allWarnings(this.target).values
            .sortedByDescending { it.timestamp })
        {
            buttons[buttons.size] = ItemBuilder
                .of(Material.PAPER)
                .name("${CC.B_GREEN}${
                    TimeUtil.formatIntoFullCalendarString(Date(warning.timestamp))
                }")
                .addToLore(
                    "${CC.WHITE}Reason: ${CC.YELLOW}${warning.reason}",
                    "${CC.WHITE}Issuer: ${CC.GOLD}${warning.issuer.username()}",
                )
                .toButton()
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = "Warnings for ${CC.PRI}${target.username()}"
}
