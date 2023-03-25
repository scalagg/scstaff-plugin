package gg.scala.staff.moderation.chat

/**
 * @author GrowlyX
 * @since 3/24/2023
 */
data class ChatControlSyncConfig(
    var chatSilenced: Boolean = false,
    var chatSlowDown: Int = 0
)
