package gg.scala.staff.notes

import com.mongodb.client.model.Filters
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.lemon.util.SplitUtil
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
object NoteService
{
    @Configure
    fun configure()
    {
        DataStoreObjectControllerCache.create<Note>()
    }

    fun fetchNotes(target: UUID): CompletableFuture<Map<UUID, Note>>
    {
        val controller = DataStoreObjectControllerCache.findNotNull<Note>()

        return controller.useLayerWithReturn<MongoDataStoreStorageLayer<Note>, CompletableFuture<Map<UUID, Note>>>(
            DataStoreStorageType.MONGO
        ) {
            return@useLayerWithReturn this.loadAllWithFilter(
                Filters.eq("target", target.toString())
            )
        }
    }

    fun addNoteToPlayer(
        issuer: Player, target: UUID,
        note: String
    )
    {
        val noteModel = Note(
            UUID.randomUUID(),
            issuer.uniqueId, target,
            Date(), note
        )

        DataStoreObjectControllerCache.findNotNull<Note>()
            .save(noteModel, DataStoreStorageType.MONGO)

        issuer.sendMessage(
            "${CC.GREEN}You've attached a note with the value ${CC.WHITE}$note${CC.GREEN} to ${CC.WHITE}${
                CubedCacheUtil.fetchName(
                    target
                )
            }'s${CC.GREEN} profile with the ID ${CC.YELLOW}#${SplitUtil.splitUuid(noteModel.uniqueId)}${CC.GREEN}."
        )
    }

    fun removeNoteFromPlayer(
        remover: Player,
        shortenedUniqueId: String
    )
    {
        val controller = DataStoreObjectControllerCache.findNotNull<Note>()

        controller.useLayer<MongoDataStoreStorageLayer<Note>>(
            DataStoreStorageType.MONGO
        ) {
            this.loadAllWithFilter(
                Filters.eq("shortenedUniqueId", shortenedUniqueId)
            ).thenAccept {
                if (it.isEmpty())
                {
                    remover.sendMessage("${CC.RED}No note with the id ${CC.YELLOW}#${shortenedUniqueId}${CC.RED} was found.")
                    return@thenAccept
                }

                val first = it.values.first()

                DataStoreObjectControllerCache.findNotNull<Note>()
                    .delete(first.uniqueId, DataStoreStorageType.MONGO)
                    .thenRun {
                        remover.sendMessage("${CC.GREEN}You've removed a note with the id ${CC.YELLOW}#${first.shortenedUniqueId}${CC.GREEN}.")
                    }
            }
        }
    }
}
