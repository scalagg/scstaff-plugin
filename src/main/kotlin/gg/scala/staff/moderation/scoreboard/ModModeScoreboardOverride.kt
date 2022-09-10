package gg.scala.staff.moderation.scoreboard

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.player.LemonPlayer
import gg.scala.staff.profile.StaffProfile
import gg.scala.staff.profile.StaffProfileService
import net.evilblock.cubed.scoreboard.ScoreboardOverride
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
object ModModeScoreboardOverride : ScoreboardOverride()
{
    override fun getTitle(player: Player) = "${CC.B_GOLD}Staff"
    override fun shouldOverride(player: Player) = player.hasMetadata("mod-mode")

    override fun getLines(board: LinkedList<String>, player: Player)
    {
        val profile = StaffProfileService
            .find(player) ?: return

        val lemonPlayer = PlayerHandler
            .find(player.uniqueId) ?: return

        board.add("${CC.GRAY}${CC.S}-------------------")
        board.add(getVanishStatus(lemonPlayer, profile))
        board.add("")

        board.add(
            "Players: ${CC.PRI}${Bukkit.getOnlinePlayers().size} ${CC.GRAY}(${
                ServerSync
                    .getLocalGameServer()
                    .getMetadataValue(
                        "lemon", "highest-player-count"
                    ) 
                    ?: 1 // the player's on the server
            })"
        )

        board.add("Channel: ${CC.PRI}${
            profile.staffChannel?.name ?: "${CC.PRI}Regular"
        }")

        board.add("Ping: ${CC.PRI}${MinecraftReflection.getPing(player)}ms")
        board.add("TPS: ${CC.GREEN}${
            String.format("%.2f", Lemon.instance.serverStatisticProvider.ticksPerSecond())
        }")

        board.add("${CC.GRAY}${CC.S}-------------------")
    }

    private fun getVanishStatus(lemonPlayer: LemonPlayer, profile: StaffProfile): String
    {
        val vanished = lemonPlayer.bukkitPlayer!!.hasMetadata("vanished")
        val hidingStaff = !profile.staffVisibilityEnabled

        var status = if (vanished) CC.RED + "Hidden" else CC.GREEN + "Visible"
        status += if (hidingStaff) " (Hiding Staff)" else " (Showing Staff)"

        return status
    }
}
