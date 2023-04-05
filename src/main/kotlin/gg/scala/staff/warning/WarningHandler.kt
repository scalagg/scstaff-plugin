package gg.scala.staff.warning

import gg.scala.lemon.Lemon
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.stream.StaffMessageStream
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
object WarningHandler
{
    data class WrappedWarn(
        val uniqueId: UUID,
        val timestamp: Long,
        val reason: String,
        val issuer: UUID
    )

    fun allWarnings(target: UUID) = ScalaCommonsSpigot.instance.kvConnection
        .sync().hgetall("staff:warnings:$target")
        .mapValues {
            Serializers.gson.fromJson(it.value, WrappedWarn::class.java)
        }

    fun warn(issuer: Player, target: UUID, reason: String) =
        CompletableFuture.runAsync {
            val wrapped = WrappedWarn(
                UUID.randomUUID(), System.currentTimeMillis(), reason, issuer.uniqueId
            )

            ScalaCommonsSpigot.instance.kvConnection
                .sync().hset(
                    "staff:warnings:$target",
                    wrapped.uniqueId.toString(),
                    Serializers.gson.toJson(wrapped)
                )

            val issuerName = QuickAccess.nameOrConsole(issuer)
            val targetName = QuickAccess.fetchColoredName(target)

            issuer.sendMessage("${ScalaStaffPlugin.PREFIX} ${CC.GREEN}You've warned $targetName${CC.GREEN} for ${CC.WHITE}$reason${CC.GREEN}.")

            QuickAccess.sendGlobalPlayerMessage(
                Lemon.instance.languageConfig
                    .warnMessage.format(reason),
                target
            )

            StaffMessageStream.send(
                "$issuerName${CC.D_AQUA} warned $targetName${CC.D_AQUA} for ${CC.AQUA}$reason${CC.D_AQUA}."
            )
        }!!
}
