package gg.scala.staff.commands

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.lemon.Lemon
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 3/24/2023
 */
fun gsdMsgExtension() = if (Lemon.instance.settings.defaultChatGsd) " for servers in the ${CC.AQUA}${ServerSync.getLocalGameServer().groups.first()}${CC.D_AQUA} group" else ""
