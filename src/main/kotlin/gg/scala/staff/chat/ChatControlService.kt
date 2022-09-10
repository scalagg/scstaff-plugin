package gg.scala.staff.chat

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.cooldown.CooldownHandler
import gg.scala.lemon.cooldown.type.PlayerCooldown
import gg.scala.lemon.listener.PlayerListener
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service
object ChatControlService
{
    var chatSilenced = false
    var chatSlowdown = 0

    object ChatCooldown : PlayerCooldown("chat")
    {
        override fun durationFor(player: Player) =
            if (player.hasPermission("scstaff.chat.cooldown.donator"))
                1000L else 3000L
    }

    object SlowChatCooldown : PlayerCooldown("slow-chat")
    {
        override fun durationFor(player: Player) =
            chatSlowdown * 1_000L
    }

    @Configure
    fun configure()
    {
        CooldownHandler.register(
            ChatCooldown, SlowChatCooldown
        )

        PlayerListener.defaultChannelProtection = context@{ event ->
            if (
                chatSilenced &&
                !event.player.hasPermission("scstaff.chat.silence.bypass")
            )
            {
                event.isCancelled = true
                event.player.sendMessage(
                    "${CC.RED}The chat is currently muted, please wait until it is no longer muted to talk."
                )
                return@context
            }

            if (
                chatSlowdown != 0 &&
                !event.player.hasPermission("scstaff.chat.slowdown.bypass")
            )
            {
                val slowChat = CooldownHandler
                    .find(SlowChatCooldown::class.java)
                    ?: return@context

                if (slowChat.isActive(event.player))
                {
                    val formatted = slowChat.getRemainingFormatted(event.player)

                    event.isCancelled = true
                    event.player.sendMessage(
                        "${CC.RED}The chat is currently slowed, please wait $formatted before talking again."
                    )
                    return@context
                }

                slowChat.addOrOverride(event.player)
                return@context
            }

            if (!event.player.hasPermission("scstaff.chat.cooldown.bypass"))
            {
                val chat = CooldownHandler
                    .find(ChatCooldown::class.java)
                    ?: return@context

                if (
                    !CooldownHandler.notifyAndContinue(
                        ChatCooldown::class.java, event.player
                    )
                )
                {
                    event.isCancelled = true
                    return@context
                }

                chat.addOrOverride(event.player)
            }
        }
    }
}
