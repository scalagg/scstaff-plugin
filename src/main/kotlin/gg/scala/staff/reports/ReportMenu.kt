package gg.scala.staff.reports

import gg.scala.flavor.inject.Inject
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.ScalaStaffPlugin
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.conversations.*
import org.bukkit.entity.Player
import java.util.UUID

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
class ReportMenu(
    private val target: UUID
) : Menu()
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    override fun getTitle(player: Player): String
    {
        return "Reporting: ${target.username()}"
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        for (reportType in ReportType.entries)
        {
            buttons[buttons.size] = ItemBuilder(reportType.material)
                .name("${CC.GREEN}${reportType.fancyName}")
                .apply {
                    reportType.examples.forEach {
                        addToLore("${CC.WHITE}${Constants.THIN_VERTICAL_LINE} ${CC.GRAY}$it")
                    }
                }
                .addToLore(
                    "",
                    "${CC.GREEN}Click to report!"
                )
                .toButton { _, _ ->
                    if (reportType == ReportType.Other)
                    {
                        val factory = ConversationFactory(plugin)
                            .withModality(true)
                            .withPrefix(NullConversationPrefix())
                            .withFirstPrompt(object : StringPrompt()
                            {
                                override fun getPromptText(context: ConversationContext): String
                                {
                                    return "${CC.GREEN}Please enter any additional information:"
                                }

                                override fun acceptInput(context: ConversationContext, input: String): Prompt
                                {
                                    return END_OF_CONVERSATION
                                }
                            })
                            .withEscapeSequence("quit")
                            .withLocalEcho(false)
                            .withTimeout(10)

                        player.closeInventory()

                        val conversation = factory.buildConversation(player)
                        conversation.cancellers.add(object : ConversationCanceller
                        {
                            override fun clone() = this

                            override fun setConversation(conversation: Conversation?)
                            {
                            }

                            override fun cancelBasedOnInput(context: ConversationContext, input: String): Boolean
                            {
                                ReportProcessor.processReport(player, target, reportType, input)
                                    .thenRun {
                                        player.sendMessage("${CC.GREEN}Your report has been submitted! A staff member will respond to this report as soon as possible.")

                                        Button.playNeutral(player)
                                    }


                                context.allSessionData["completed"] = true
                                return true
                            }
                        })

                        conversation.addConversationAbandonedListener {
                            if (it.context.getSessionData("completed") == true)
                            {
                                return@addConversationAbandonedListener
                            }

                            player.sendMessage("${CC.RED}Your report was cancelled as you didn't prove a reason within a reasonable amount of time.")
                        }

                        player.beginConversation(conversation)
                    } else
                    {
                        player.closeInventory()

                        ReportProcessor
                            .processReport(player, target, reportType)
                            .thenRun {
                                player.sendMessage("${CC.GREEN}Your report has been submitted! A staff member will respond to this report as soon as possible.")

                                Button.playNeutral(player)
                            }
                    }
                }
        }

        return buttons
    }
}
