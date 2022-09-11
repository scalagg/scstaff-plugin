package gg.scala.staff.channel

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.channel.ChatChannelBuilder
import gg.scala.lemon.channel.ChatChannelService
import gg.scala.staff.profile.StaffProfileService

/**
 * @author GrowlyX
 * @since 4/6/2022
 */
@Service
object StaffChatChannelService
{
    @Configure
    fun configure()
    {
        StaffChatChannelType.values()
            .forEach { register(it) }
    }

    private fun register(
        channelType: StaffChatChannelType
    )
    {
        val composite = StaffChatChannelComposite(channelType)
        val channelName = channelType.name.lowercase()

        val channel = ChatChannelBuilder
            .newBuilder()
            .import(composite)
            .compose()
            .forceRealRank()
            .distribute()
            .triggerPrefixed(
                channelType.prefix
            )
            .allowOnlyIf {
                val lemonPlayer = StaffProfileService.find(it)
                    ?: return@allowOnlyIf false

                it.hasPermission("scstaff.channel.$channelName") && lemonPlayer.staffMessagesEnabled
            }
            .override(1) {
                StaffProfileService.find(it)?.staffChannel == channelType
            }

        ChatChannelService.register(channel)
    }
}
