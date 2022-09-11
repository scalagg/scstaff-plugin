package gg.scala.staff.moderation.frozen.event

import gg.scala.commons.event.StatelessEvent
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
data class PostFreezeEvent(
    val player: Player
) : StatelessEvent()
