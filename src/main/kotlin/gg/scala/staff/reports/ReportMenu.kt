package gg.scala.staff.reports

import gg.scala.lemon.cooldown.CooldownHandler
import gg.scala.lemon.cooldown.impl.ReportCooldown
import gg.scala.lemon.util.QuickAccess
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
class ReportMenu(
    private val target: Player
) : Menu()
{
    override fun getTitle(player: Player): String
    {
        return "Reporting ${target.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        for (reportType in ReportType.values())
        {
            buttons[buttons.size] = ItemBuilder(reportType.material)
                .name("${CC.B_GREEN}${reportType.fancyName}")
                .addToLore(
                    "${CC.WHITE}${reportType.examples.joinToString(", ")}",
                    "",
                    "${CC.GREEN}Click to report!"
                )
                .toButton { _, _ ->


                    QuickAccess.sendStaffMessageWithFlag(
                        "${CC.YELLOW}${player.name}${CC.RED} has reported ${CC.YELLOW}${
                            QuickAccess.coloredName(
                                target
                            )
                        }${CC.RED} for ${CC.GOLD}${reportType.fancyName}${CC.RED}.",
                        true, "reports-disabled"
                    ).whenComplete { _, throwable ->
                        if (throwable != null)
                        {
                            player.sendMessage("${CC.RED}Something went wrong while processing your report, try again later.")
                        } else
                        {
                            val report = CooldownHandler.find(
                                ReportCooldown::class.java
                            )

                            report?.addOrOverride(player)

                            player.closeInventory()
                            player.sendMessage("${CC.GREEN}Your report has been submitted.")
                        }
                    }
                }
        }

        return buttons
    }
}
