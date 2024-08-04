package gg.scala.staff.commands.testing

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.staff.anticheat.AnticheatFeature

/**
 * @author GrowlyX
 * @since 7/25/2024
 */
@AutoRegister
object ACTestCommand : ScalaCommand()
{
    @CommandAlias("test-anticheat")
    @CommandPermission("op")
    fun onTest(player: ScalaPlayer)
    {
        AnticheatFeature.onAnticheatAlert(AnticheatFeature.AbstractAnticheatAlert(
            player.bukkit(),
            "Hors",
            14.0,
            "hors is mean"
        ))
    }

}
