package gg.scala.staff.moderation.scoreboard

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.lemon.Lemon
import gg.scala.lemon.LemonConstants
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.handler.RankHandler
import gg.scala.lemon.player.LemonPlayer
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.profile.StaffProfile
import gg.scala.staff.profile.StaffProfileService
import net.evilblock.cubed.scoreboard.ScoreboardOverride
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
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
    override fun getTitle(player: Player) = ModModeScoreboardAnimation.getCurrentTitle()
    override fun shouldOverride(player: Player) = player.hasMetadata("mod-mode")

    override fun getLines(board: LinkedList<String>, player: Player)
    {
        val profile = StaffProfileService
            .find(player) ?: return

        val lemonPlayer = PlayerHandler
            .find(player.uniqueId) ?: return

        val rank = lemonPlayer.disguiseRank()
            ?: QuickAccess.computeRank(player.uniqueId).join()!!

        val hidingStaff = !profile.staffVisibilityEnabled

        val vanished = lemonPlayer.bukkitPlayer!!.hasMetadata("vanished")

        board.add("")
        board.add("${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}Rank: ${CC.PRI}${rank.getColoredName()}")
        board.add("${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}Channel: ${profile.staffChannel?.chatColor}${
            profile.staffChannel?.name ?: "${CC.GREEN}Public"
        }")
        board.add("${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}Ping: ${CC.PRI}${MinecraftReflection.getPing(player)}ms")
        board.add("${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}Vanish: ${if (vanished) CC.GREEN + Constants.CHECK_SYMBOL else CC.RED + Constants.X_SYMBOL}")
        board.add("${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}Showing Staff: ${if (hidingStaff) CC.RED + Constants.X_SYMBOL else CC.GREEN + Constants.CHECK_SYMBOL}")
        board.add("")
        board.add(
            "${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}Players: ${CC.PRI}${Bukkit.getOnlinePlayers().size} ${CC.GRAY}(${
                ServerSync
                    .getLocalGameServer()
                    .getMetadataValue(
                        "lemon", "highest-player-count"
                    )
                    ?: 1 // the player's on the server
            })"
        )
        board.add("${CC.GRAY}${Constants.THICK_VERTICAL_LINE} ${CC.WHITE}TPS: ${CC.GREEN}${
            String.format("%.2f", Lemon.instance.serverStatisticProvider.ticksPerSecond())
        }")

        board.add("")
        board.add("${CC.GRAY}${LemonConstants.WEB_LINK}")
    }
}
