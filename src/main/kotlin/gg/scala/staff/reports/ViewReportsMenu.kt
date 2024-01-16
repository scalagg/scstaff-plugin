package gg.scala.staff.reports

import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.lemon.util.SplitUtil
import gg.scala.staff.reports.model.PlayerReport
import gg.scala.staff.reports.model.Priority
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.text.TextSplitter
import net.evilblock.cubed.util.time.DateUtil
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/16/2024
 */
data class ViewReportsMenu(
    private val reports: List<PlayerReport>,
    private val source: String = ""
) : PaginatedMenu()
{
    init
    {
        async = true
    }

    override fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)
    {
        callback(true)
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        for (report in reports)
        {
            val userIsOnline = QuickAccess.online(report.to).join()
            val lastLogin = QuickAccess.getLastOnline(report.to).join()
                ?: System.currentTimeMillis()

            buttons[buttons.size] = ItemBuilder
                .of(report.priority.iconMaterial)
                .name(
                    "${CC.GREEN}#${
                        SplitUtil.splitUuid(report.identifier)
                    }"
                )
                .addToLore(
                    "${CC.GRAY}Submitted: ${CC.WHITE}${
                        DateUtil.formatSimplifiedDateDiff(report.timestamp.epochMillis)
                    } ago",
                    "${CC.GRAY}Priority: ${
                        if (report.priority == Priority.High) "${CC.D_RED}High" else "${CC.D_GRAY}Normal"
                    }",
                    "",
                    "${CC.GRAY}From: ${CC.WHITE}${report.from.username()}",
                    "${CC.GRAY}Reported: ${CC.WHITE}${report.to.username()}",
                    " ${CC.GRAY}(${
                        if (userIsOnline)
                        {
                            "${CC.GREEN}on ${
                                QuickAccess.server(report.to).join()?.id ?: "???"
                            }"
                        } else
                        {
                            "${CC.RED}offline for ${
                                DateUtil.formatSimplifiedDateDiff(lastLogin)
                            }"
                        }
                    }${CC.GRAY})",
                    "",
                    "${CC.GREEN}Related accounts:${
                        if (report.relatedAccounts.isEmpty()) " ${CC.RED}None" else ""
                    }"
                )
                .apply {
                    if (report.relatedAccounts.isNotEmpty())
                    {
                        report.relatedAccounts.take(3)
                            .forEach {
                                addToLore("${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${it.username()}")
                            }

                        if (report.relatedAccounts.size > 3)
                        {
                            addToLore("${CC.GRAY}${Constants.THIN_VERTICAL_LINE} (and ${
                                report.relatedAccounts - 3
                            } more...)")
                        }
                    }
                }
                .addToLore(
                    "",
                    "${CC.GRAY}Category: ${CC.GOLD}${report.category.fancyName}"
                )
                .apply {
                    if (report.playerProvidedContext != null)
                    {
                        addToLore(
                            *TextSplitter
                                .split(
                                    report.playerProvidedContext,
                                    " ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}",
                                    " ${CC.WHITE}"
                                )
                                .toTypedArray()
                        )
                    }
                }
                .addToLore(
                    "${CC.GRAY}Server: ${CC.WHITE}${report.fromServer}",
                    "",
                    "${CC.YELLOW}Click to resolve!"
                )
                .apply {
                    if (report.priority != Priority.High)
                    {
                        addToLore("${CC.RED}Shift-Click to escalate!")
                    }
                }
                .toButton { _, type ->
                    if (type!!.isShiftClick)
                    {
                        if (report.priority != Priority.Normal)
                        {
                            return@toButton
                        }

                        report.priority = Priority.High
                        report.save().thenRun {
                            Button.playSuccess(player)
                            openMenu(player)
                        }
                        return@toButton
                    }

                    Button.playSuccess(player)

                    player.closeInventory()
                    player.sendMessage("${CC.RED}The report has been resolved!")

                    DataStoreObjectControllerCache
                        .findNotNull<PlayerReport>()
                        .delete(
                            report.identifier,
                            DataStoreStorageType.MONGO
                        )
                }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) =
        "Viewing unresolved reports$source..."
}
