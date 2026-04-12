package gg.scala.staff.moderation.frozen.event

import gg.scala.commons.event.StatefulEvent
import gg.scala.commons.event.StatelessEvent
import org.bukkit.entity.Player

data class ModModeEnterEvent(
    val player: Player
) : StatefulEvent()
