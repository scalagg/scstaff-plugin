package gg.scala.staff.reports

import gg.scala.lemon.util.QuickAccess.username
import gg.scala.staff.stream.StaffMessageStream
import me.lucko.helper.cooldown.Cooldown
import me.lucko.helper.cooldown.CooldownMap
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
object ReportProcessor
{
    val coolDowns = CooldownMap
        .create<UUID>(
            Cooldown.of(90L, TimeUnit.SECONDS)
        )

    fun processReport(
        player: Player, target: UUID, reason: String
    )
    {
        StaffMessageStream.send(
            "${CC.YELLOW}${player.displayName}${CC.RED} has reported ${target.username()} for ${CC.GOLD}$reason${CC.RED}.",
            mustNotHaveMetadata = "reports-disabled"
        )
    }
}
