package gg.scala.staff.moderation.frozen

import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.staff.moderation.frozen.FrozenPlayerHandler.expirables
import gg.scala.lemon.util.QuickAccess
import gg.scala.staff.stream.StaffMessageStream
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.util.CC
import java.util.*

@Repeating(20L, context = ThreadContext.ASYNC)
object FrozenPlayerUpdates : Runnable
{
    override fun run()
    {
        expirables.toMap()
            .forEach { (uuid, expirable) ->
                if (expirable.hasExpired)
                {
                    StaffMessageStream.send(
                        "${CC.AQUA}${QuickAccess.coloredName(uuid)}${CC.D_AQUA} has been frozen for 5 minutes.", true
                    )

                    expirables.remove(uuid)
                }
            }
    }
}
