package gg.scala.staff

import gg.scala.commons.config.annotations.Config
import gg.scala.lemon.LemonConstants
import net.evilblock.cubed.util.CC
import xyz.mkotb.configapi.Coloured

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Config("config")
class ScalaStaffConfig
{
    val reportsUiBased = false
    val frozenAutoBan = false

    @Coloured
    var frozenPlayerHasTimeMessage = """
        ${CC.RED}  
        ${CC.RED}You've been frozen by a staff member!
        ${CC.YELLOW}You have ${CC.WHITE}%s${CC.YELLOW} to join ${CC.GREEN}discord.${LemonConstants.WEB_LINK}${CC.YELLOW}.
        ${CC.GRAY}If you fail to comply with our staff team's orders, you will be banned.
        ${CC.RED}  
    """.trimIndent()

    @Coloured
    var frozenPlayerTimeIsUpMessage = """
        ${CC.RED}  
        ${CC.RED}You've been frozen by a staff member!
        ${CC.YELLOW}It has been 5 minutes! Please join as soon as possible.
        ${CC.GRAY}If you fail to comply with our staff team's orders, you will be banned.
        ${CC.RED}  
    """.trimIndent()
}
