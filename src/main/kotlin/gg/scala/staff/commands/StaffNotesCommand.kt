package gg.scala.staff.commands

import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.*
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AssignPermission
import gg.scala.staff.notes.NotePaginatedResult
import gg.scala.staff.notes.NoteService
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 10/8/2021
 */
@AutoRegister
@CommandPermission("scstaff.command.notes")
@CommandAlias("note|notes|profilenotes|staffnotes")
object StaffNotesCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("list")
    @CommandCompletion("@players")
    @Description("List all notes attached to a player.")
    fun onList(sender: CommandSender, uniqueId: UUID, @Optional page: Int?)
    {
        sender.sendMessage(
            "${CC.SEC}Loading notes for ${CC.PRI}${
                CubedCacheUtil.fetchName(uniqueId)
            }${CC.SEC}..."
        )

        NoteService.fetchNotes(target = uniqueId)
            .thenAccept {
                NotePaginatedResult.display(
                    sender, listOf(*it.values.toTypedArray()),
                    page ?: 1, command = "notes list ${CubedCacheUtil.fetchName(uniqueId)} %s"
                )
            }
    }

    @AssignPermission
    @Subcommand("add")
    @CommandCompletion("@players")
    @Description("Attach a note to a player.")
    fun onAdd(player: Player, uniqueId: UUID, note: String)
    {
        NoteService.addNoteToPlayer(player, uniqueId, note)
    }

    @AssignPermission
    @Subcommand("remove")
    @CommandCompletion("@players")
    @Description("Remove a note from a player.")
    fun onRemove(player: Player, @Single id: String)
    {
        NoteService.removeNoteFromPlayer(player, id)
    }
}
