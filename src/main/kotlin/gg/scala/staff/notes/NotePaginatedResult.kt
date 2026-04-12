package gg.scala.staff.notes

import gg.scala.lemon.util.CubedCacheUtil
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.PaginatedResult

object NotePaginatedResult : PaginatedResult<Note>()
{
    override fun format(result: Note, resultIndex: Int): String
    {
        return "${CC.PRI}#$resultIndex ${CC.WHITE}- ${CC.SEC}${CubedCacheUtil.fetchName(result.issuer)} ${CC.GRAY}(${result.shortenedUniqueId})${CC.WHITE}: ${result.value}"
    }

    override fun getHeader(page: Int, maxPages: Int): String
    {
        return "${CC.PRI}=== ${CC.SEC}Notes ${CC.GRAY}($page/$maxPages) ${CC.PRI}==="
    }
}
