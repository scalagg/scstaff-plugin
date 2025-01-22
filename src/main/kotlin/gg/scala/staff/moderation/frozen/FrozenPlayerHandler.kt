package gg.scala.staff.moderation.frozen

import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import gg.scala.lemon.listener.PlayerListener
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.lemon.util.other.Expirable
import gg.scala.staff.config
import gg.scala.staff.moderation.frozen.event.PostFreezeEvent
import gg.scala.staff.stream.StaffMessageStream
import me.lucko.helper.Events
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/23/2021
 */
@Service
@Repeating(100L, context = ThreadContext.ASYNC)
object FrozenPlayerHandler : Runnable
{
    val expirables = mutableMapOf<UUID, FrozenExpirable>()

    @Configure
    fun configure()
    {
        Events.subscribe(PostFreezeEvent::class.java)
            .handler {
//                FrozenPlayerMenu().openMenu(it.player)
            }

        Events
            .subscribe(PlayerJoinEvent::class.java)
            .handler {
                it.player.removeMetadata("frozen", Lemon.instance)
            }

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                val isFrozen = it.player.hasMetadata("frozen")

                if (isFrozen)
                {
                    StaffMessageStream.send(
                        FancyMessage().withMessage(
                            "${CC.AQUA}${QuickAccess.coloredName(it.player)}${CC.D_AQUA} logged out while frozen. ${CC.GRAY}[${CC.RED}Ban${CC.GRAY}]")
                            .andHoverOf(
                                "${CC.GREEN}Click to ban ${QuickAccess.coloredName(it.player)}"
                            )
                            .andCommandOf(
                                ClickEvent.Action.RUN_COMMAND,
                                "/ban ${it.player.uniqueId.username()} 30d Logged out whilst frozen"
                            ).toString()
                    )

                    it.player.removeMetadata("frozen", Lemon.instance)

                    expirables.remove(it.player.uniqueId)

                    if (config().frozenAutoBan)
                    {
                        Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "/ban ${it.player.uniqueId.username()} 30d Logged out whilst frozen"
                        )
                        return@handler
                    }
                }
            }
    }

    override fun run()
    {
        Bukkit.getOnlinePlayers()
            .filter { it.hasMetadata("frozen") }
            .forEach {
                val expirable = expirables[it.uniqueId]

                if (expirable == null)
                {
                    it.sendMessage(
                       config().frozenPlayerTimeIsUpMessage
                    )
                } else
                {
                    it.sendMessage(
                        config().frozenPlayerHasTimeMessage.replace(
                            "%s", expirable.durationFromNowStringRaw
                        )
                    )
                }
            }
    }

    class FrozenExpirable : Expirable(
        System.currentTimeMillis(),
        TimeUnit.MINUTES.toMillis(5)
    )
}
