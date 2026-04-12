package gg.scala.staff.moderation.chat

data class ChatControlSyncConfig(
    var chatSilenced: Boolean = false,
    var chatSlowDown: Int = 0
)
