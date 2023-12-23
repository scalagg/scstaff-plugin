package gg.scala.staff.reports

import gg.scala.flavor.inject.Inject
import gg.scala.staff.ScalaStaffPlugin
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.conversations.*
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
class ReportMenu(
    private val target: Player
) : Menu()
{
    @Inject
    lateinit var plugin: ScalaStaffPlugin

    override fun getTitle(player: Player): String
    {
        return "Reporting ${target.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        for (reportType in ReportType.entries)
        {
            buttons[buttons.size] = ItemBuilder(reportType.material)
                .name("${CC.B_GREEN}${reportType.fancyName}")
                .addToLore(
                    "${CC.WHITE}${reportType.examples.joinToString(", ")}",
                    "",
                    "${CC.GREEN}Click to report!"
                )
                .toButton { _, _ ->
                    if (reportType == ReportType.OTHER)
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
                                    ReportProcessor.processReport(player, target.uniqueId, input)
                                    player.closeInventory()
                                    player.sendMessage("${CC.GREEN}Your report has been submitted!")

                                    return Prompt.END_OF_CONVERSATION
                                }

                            })
                            .withEscapeSequence("/no")
                            .withLocalEcho(false)
                            .withTimeout(5)

                        val conversation = factory.buildConversation(player)

                        conversation.addConversationAbandonedListener {
                            player.sendMessage("${CC.RED}Your report was cancelled as you didn't prove a reason within a reasonable amount of time.")
                        }

                        player.beginConversation(conversation)
                    } else
                    {
                        ReportProcessor.processReport(player, target.uniqueId, reportType.fancyName)
                        player.closeInventory()
                        player.sendMessage("${CC.GREEN}Your report has been submitted!")
                    }
                }
        }

        return buttons
    }
}
