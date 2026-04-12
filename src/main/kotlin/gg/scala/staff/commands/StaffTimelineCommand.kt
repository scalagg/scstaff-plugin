package gg.scala.staff.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.handler.GrantHandler
import gg.scala.lemon.player.grant.Grant
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.coloredNameOrNull
import gg.scala.lemon.util.QuickAccess.computeColoredName
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.time.TimeUtil
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * @date 3/20/2025
 */
@AutoRegister
object StaffTimelineCommand : ScalaCommand() {

    val dateFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
    var blockColors: String = "▋"
    val COMPARATOR: Comparator<Grant> = Comparator.comparingLong(::addedAt)

    fun getDate(date: Long): String {
        return dateFormat.format(date)
    }

    fun addedAt(grant: Grant): Long {
        return grant.addedAt
    }

    @CommandAlias("timeline|stafftimeline")
    @CommandCompletion("@players")
    @CommandPermission("scstaff.staff-member")
    fun onTimeline(player: ScalaPlayer, @Optional target: AsyncLemonPlayer) =
        target.validatePlayers(player.bukkit(), false) { lemonPlayer ->
            val totalGrants = GrantHandler
                .fetchGrantsFor(lemonPlayer.uniqueId)
                .join()

            val grants = totalGrants
                .filter { it.getRank().weight >= 1599 }
                .sortedBy { it.addedAt }
                .toMutableList()

            val blockLines: MutableList<String> = mutableListOf()
            val logs: MutableList<String> = mutableListOf()

            if (grants.isEmpty()) {
                throw ConditionFailedException("Specified player has not been staff.")
            }

            var joinedTeam = -1L
            var lastRemoveTime = -1L
            var lastGrant = -1L
            var lastWeight = -1

            val messages = mutableListOf<String>().apply { add("") }

            for (grant in grants) {
                val rank = grant.getRank()
                val addedBy = grant.addedBy?.let { QuickAccess.coloredName(it) } ?: "Console"
                val addedAtDate = getDate(grant.addedAt)

                blockLines.add(
                    (grant.getRank().color
                        .replace("§o", "") + CC.STRIKE_THROUGH) + blockColors + "" + CC.RESET
                )
                for (i in 0 until abs((TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - grant.addedAt) / 7).toDouble())
                    .toInt()) {
                    blockLines.add((CC.DARK_GRAY + CC.STRIKE_THROUGH) + blockColors + CC.RESET)
                }

                if (joinedTeam == -1L) {
                    joinedTeam = grant.addedAt
                    logs.add("${CC.WHITE}$addedAtDate ${CC.GRAY}${Constants.CURVED_ARROW_RIGHT} ${CC.YELLOW}Joined as ${rank.color}${rank.name}${CC.YELLOW}.")
                } else {
                    when {
                        rank.weight > lastWeight -> logs.add("${CC.WHITE}$addedAtDate ${CC.GRAY}${Constants.CURVED_ARROW_RIGHT} ${CC.GREEN}Promoted ${CC.YELLOW}to ${rank.color}${rank.name}${CC.YELLOW}.")
                        rank.weight == lastWeight -> logs.add("${CC.WHITE}$addedAtDate ${CC.GRAY}${Constants.CURVED_ARROW_RIGHT} ${CC.YELLOW}Granted ${rank.color}${rank.name}${CC.YELLOW}.")
                        else -> logs.add("${CC.WHITE}$addedAtDate ${CC.GRAY}${Constants.CURVED_ARROW_RIGHT} ${CC.RED}Demoted ${CC.YELLOW}to ${rank.color}${rank.name}${CC.YELLOW}.")
                    }
                }

                if (lastGrant == -1L) lastGrant = grant.addedAt

                lastGrant = grant.addedAt
                lastRemoveTime = if (grant.isRemoved) grant.removedAt else -1L
                lastWeight = rank.weight
            }

            val result = blockLines.joinToString("")

            messages.add("${CC.B_PRI}Staff Timeline ${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${lemonPlayer.getOriginalColoredName()}")

            if (lastRemoveTime != -1L) {
                messages.add("${CC.SEC}Parted ways on ${CC.WHITE}${getDate(lastRemoveTime)}${CC.YELLOW}.")
                messages.add("${CC.SEC}Helped for ${CC.WHITE}${TimeUtil.millisToRoundedTime(lastRemoveTime - joinedTeam)}${CC.YELLOW}.")
            } else {
                messages.add("${CC.SEC}Helping for ${CC.WHITE}${TimeUtil.millisToRoundedTime(System.currentTimeMillis() - joinedTeam)}${CC.YELLOW}.")
            }
            messages.add("")
            messages.add("${CC.PRI}Progress:")
            messages.add(result)
            messages.add("")
            messages.add("${CC.PRI}Journey:")
            messages.addAll(logs)
            messages.add("")
            messages.forEach(player::sendMessage)
        }
}