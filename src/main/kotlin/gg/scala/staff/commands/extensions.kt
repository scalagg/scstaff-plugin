package gg.scala.staff.commands

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.lemon.Lemon
import net.evilblock.cubed.util.CC

fun gsdMsgExtension() = if (Lemon.instance.settings.defaultChatGsd) " for servers in the ${CC.AQUA}${Lemon.instance.settings.defaultChatGsdGroupId}${CC.D_AQUA} group" else ""
