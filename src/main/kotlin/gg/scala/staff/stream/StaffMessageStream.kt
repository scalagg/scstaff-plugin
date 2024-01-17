package gg.scala.staff.stream

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service(priority = 100)
object StaffMessageStream
{
    @JvmStatic
    val CHANNEL = "${ScalaStaffPlugin.NAMESPACE}:messages"

    @Inject
    lateinit var plugin: ScalaStaffPlugin

    val aware by lazy {
        AwareBuilder
            .of<AwareMessage>(CHANNEL)
            .logger(this.plugin.logger)
            .codec(AwareMessageCodec)
            .build()
    }

    fun send(
        message: String, includeOrigin: Boolean = true,
        mustNotHaveMetadata: String? = null,
        context: AwareThreadContext = AwareThreadContext.ASYNC
    )
    {
        AwareMessage.of(
            "staff-message", this.aware,
            "message" to message,
            "from" to ServerSync
                .getLocalGameServer().id,
            "includeFrom" to includeOrigin,
            "additionalFlag" to mustNotHaveMetadata
        ).publish(
            channel = CHANNEL, context = context
        )
    }

    fun sendComplex(
        message: Message, includeOrigin: Boolean = true,
        mustNotHaveMetadata: String? = null,
        context: AwareThreadContext = AwareThreadContext.ASYNC
    )
    {
        AwareMessage.of(
            "staff-message", this.aware,
            "message" to message,
            "from" to ServerSync
                .getLocalGameServer().id,
            "includeFrom" to includeOrigin,
            "additionalFlag" to mustNotHaveMetadata
        ).publish(
            channel = CHANNEL, context = context
        )
    }

    inline fun <reified T : Any> AwareMessage.handleStaffMessageOfDelivery(
        delivery: (Player, String, T) -> Unit
    )
    {
        val content = this.retrieve<T>("message")

        val from = this.retrieve<String>("from")
        val includeFrom = this.retrieve<Boolean>("includeFrom")

        val additionalFlag = this
            .retrieveNullable<String>("additionalFlag")

        val baseMessage = "${ChatColor.AQUA}[S] ${
            if (includeFrom) "${ChatColor.DARK_AQUA}[$from] " else ""
        }"

        Bukkit.getOnlinePlayers().filter {
            val profile = StaffProfileService.find(it)
                ?: return@filter false

            val ableToReceive = it.hasPermission(ScalaStaffPlugin.STAFF_NODE) &&
                profile.staffMessagesEnabled

            val flag = if (additionalFlag == null) true else
                if (additionalFlag == "reports-disabled") profile.staffReportNotificationsEnabled else
                    !plugin.settingProvider.provideSetting(it, additionalFlag)

            ((additionalFlag == null || additionalFlag == "null") || flag) && ableToReceive
        }.forEach {
            delivery(it, baseMessage, content)
        }
    }

    @Configure
    fun configure()
    {
        this.aware.listen("staff-message") {
            handleStaffMessageOfDelivery<String> { player, prefix, message ->
                player.sendMessage("$prefix$message")
            }
        }

        this.aware.listen("staff-message-complex") {
            handleStaffMessageOfDelivery<Message> { player, prefix, message ->
                val newMessage = Message()
                newMessage.components += message.components

                val firstComponent = newMessage.components.first()
                firstComponent.value = "$prefix${firstComponent.value}"
                newMessage.sendToPlayer(player)
            }
        }

        this.aware.connect()
    }
}
