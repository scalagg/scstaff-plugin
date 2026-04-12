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
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.text.TextSplitter
import net.evilblock.cubed.util.time.DateUtil
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

data class ViewReportsMenu(
    private val reports: () -> CompletableFuture<List<PlayerReport>>,
    private val source: String = ""
) : PaginatedMenu()
{
    init
    {
        async = true
    }

    private var listOfReports: List<PlayerReport> = listOf()
    override fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)
    {
        reports().thenAccept {
            listOfReports = it
            callback(true)
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        for (report in listOfReports)
        {
            val userIsOnline = QuickAccess.online(report.to).join()
            val lastLogin = QuickAccess.getLastOnline(report.to).join()
                ?: System.currentTimeMillis()

            val responseActions = ReportProcessor
                .reduceAllActionProviders(player, report)

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
                                addToLore(" ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${it.username()}")
                            }

                        if (report.relatedAccounts.size > 3)
                        {
                            addToLore(" ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} (and ${
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
                )
                .apply {
                    if (report.serverProvidedContext.isNullOrEmpty().not())
                    {
                        report.serverProvidedContext!!.split(", ")
                            .forEach {
                                addToLore(" ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}$it")
                            }
                    }

                    addToLore("")
                    if (responseActions.isNotEmpty())
                    {
                        addToLore("${CC.YELLOW}Click to take action!")
                    }

                    addToLore("${CC.GOLD}Right-Click to resolve!")
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

                    if (type.isLeftClick && responseActions.isNotEmpty())
                    {
                        player.closeInventory()
                        player.sendMessage("${CC.D_GREEN}${CC.STRIKE_THROUGH}${"-".repeat(45)}")
                        player.sendMessage("${CC.GREEN}Take Action ${CC.GRAY}(click the messages)${CC.GREEN}:")

                        responseActions.forEach {
                            val fancyMessage = FancyMessage()
                                .withMessage(
                                    "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}${CC.UNDERLINE}"
                                )
                                .withMessage(it.description)
                                .andHoverOf(
                                    "${CC.RED}Click to select this action!"
                                )
                                .andCommandOf(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/${it.command}"
                                )

                            fancyMessage.sendToPlayer(player)
                        }

                        player.sendMessage("${CC.D_GREEN}${CC.STRIKE_THROUGH}${"-".repeat(45)}")
                        return@toButton
                    }

                    if (type.isRightClick)
                    {
                        DataStoreObjectControllerCache
                            .findNotNull<PlayerReport>()
                            .delete(
                                report.identifier,
                                DataStoreStorageType.MONGO
                            )
                            .thenRun {
                                player.sendMessage("${CC.RED}The report has been resolved!")
                                Button.playSuccess(player)

                                openMenu(player)
                            }
                            .thenComposeAsync {
                                QuickAccess.sendGlobalPlayerMessage(
                                    "${CC.GREEN}Your report to ${report.to.username()} has been resolved by a staff member. Thank you for helping keep our server safe!",
                                    report.from
                                )
                            }
                    }
                }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) =
        "Viewing unresolved reports${
            if (source.isNotEmpty()) " $source" else ""
        }..."
}
