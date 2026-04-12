package gg.scala.staff.moderation.jump

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.UUID

/**
 * @author Limo
 * @date 2/5/2026
 */
class PlayerExpectedToJumpEvent(
    val player: UUID,
    val target: UUID,
    val parameters: Map<String, String> = emptyMap()
) : Event(true)
{
    companion object
    {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList
}
