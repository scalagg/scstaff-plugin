package gg.scala.staff.stream

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service
object StaffMessageStream
{
    @JvmStatic
    val CHANNEL = "${ScalaStaffPlugin.NAMESPACE}:messages"

    @Inject
    lateinit var plugin: ScalaStaffPlugin

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>(CHANNEL)
            .logger(this.plugin.logger)
            .codec(AwareMessageCodec)
            .build()
    }

    fun send(
        message: String, includeOrigin: Boolean = true,
        additionalPermission: String? = null,
        context: AwareThreadContext = AwareThreadContext.ASYNC
    )
    {
        AwareMessage.of(
            "staff-message", this.aware,
            "message" to message,
            "from" to ServerSync
                .getLocalGameServer().id,
            "includeFrom" to includeOrigin,
            "additionalFlag" to additionalPermission
        ).publish(
            channel = CHANNEL, context = context
        )
    }

    @Configure
    fun configure()
    {
        this.aware.listen("staff-message") {
            val content = this.retrieve<String>("message")

            val from = this.retrieve<String>("from")
            val includeFrom = this.retrieve<Boolean>("includeFrom")

            val additionalFlag = this
                .retrieveNullable<String>("additionalFlag")

            val baseMessage = "${ChatColor.AQUA}[S] ${
                if (includeFrom) "${ChatColor.DARK_AQUA}[$from] " else ""
            }"

            Bukkit.getOnlinePlayers().filter {
                val lemonPlayer = PlayerHandler
                    .find(it.uniqueId)
                    ?: return@filter false

                val profile = StaffProfileService.find(it)
                    ?: return@filter false

                val ableToReceive = it.hasPermission(ScalaStaffPlugin.STAFF_NODE) &&
                        profile.staffMessagesEnabled

                ((additionalFlag == null || additionalFlag == "null") ||
                        lemonPlayer.hasPermission(additionalFlag)) && ableToReceive
            }.forEach {
                it.sendMessage("$baseMessage$content")
            }
        }

        this.aware.connect()
    }
}
