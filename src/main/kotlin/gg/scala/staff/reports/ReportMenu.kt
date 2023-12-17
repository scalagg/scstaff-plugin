package gg.scala.staff.reports

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
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
                    var reason = "Other"

                    if (reportType == ReportType.OTHER)
                    {
                        InputPrompt()
                            .withText("${CC.GREEN}Please enter any additional information:")
                            .acceptInput { _, result ->
                                reason = result
                            }.start(player)
                    } else reason = reportType.fancyName

                    player.closeInventory()
                    ReportProcessor.processReport(player, target.uniqueId, reason)
                    player.sendMessage("${CC.GREEN}Your report has been submitted!")
                }
        }

        return buttons
    }
}
