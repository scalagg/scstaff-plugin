package gg.scala.staff.commands.chat

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.acf.annotation.Syntax
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.ScalaStaffPlugin
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ThreadLocalRandom

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@AutoRegister
object ChatClearCommand : ScalaCommand()
{
    @Syntax("[-h]")
    @CommandAlias("clearchat|chatclear|cc")
    @CommandPermission("scstaff.command.clearchat")
    fun onClearChat(
        sender: CommandSender,
        @Optional hiddenString: String?
    ): CompletableFuture<Void>
    {
        val coloredName = QuickAccess.nameOrConsole(sender)
        val hidden = hiddenString != null && hiddenString == "-h"

        return CompletableFuture.runAsync {
            Bukkit.getOnlinePlayers()
                .filter { !it.hasPermission(ScalaStaffPlugin.STAFF_NODE) }
                .forEach {
                    for (int in 0..250)
                    {
                        it.sendMessage(
                            " ".repeat(
                                ThreadLocalRandom.current().nextInt(1, 50)
                            )
                        )
                    }
                }
        }.thenRun {
            Bukkit.broadcastMessage(
                "${CC.GREEN}The chat has been cleared by ${if (hidden) "staff" else coloredName}${CC.GREEN}."
            )

            QuickAccess.sendStaffMessage(
                "$coloredName${CC.D_AQUA} has cleared chat.", true
            )
        }
    }
}
