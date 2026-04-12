package gg.scala.staff.anticheat

import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Limo
 * @date 2/5/2026
 */
data class AnticheatProfile(
    val uniqueId: UUID,
    val logs: MutableMap<String, MutableList<AnticheatLog>> = mutableMapOf()
)
{
    fun addLog(log: AnticheatLog)
    {
        logs.getOrPut(log.check) { mutableListOf() }.add(log)
    }

    fun getLogsForCheck(check: String): List<AnticheatLog>
    {
        return logs[check] ?: emptyList()
    }

    fun getAllLogs(): List<AnticheatLog>
    {
        return logs.values.flatten()
    }

    fun getTotalViolations(): Int
    {
        return logs.values.sumOf { it.sumOf(AnticheatLog::violations) }
    }
}

/**
 * @author Limo
 * @date 2/5/2026
 */
object AnticheatProfileService
{
    private val profiles = ConcurrentHashMap<UUID, AnticheatProfile>()

    fun findOrFetch(player: UUID): CompletableFuture<AnticheatProfile?>
    {
        return CompletableFuture.completedFuture(
            profiles.getOrPut(player) { AnticheatProfile(uniqueId = player) }
        )
    }

    fun find(player: UUID): AnticheatProfile?
    {
        return profiles.getOrPut(player) { AnticheatProfile(uniqueId = player) }
    }

    fun addLog(player: UUID, log: AnticheatLog)
    {
        val profile = profiles.getOrPut(player) { AnticheatProfile(uniqueId = player) }
        profile.addLog(log)
    }

    fun clear(player: UUID)
    {
        profiles.remove(player)
    }
}
