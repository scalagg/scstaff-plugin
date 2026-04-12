package gg.scala.staff.anticheat

/**
 * @author Limo
 * @date 2/5/2026
 */
data class AnticheatLog(
    val check: String,
    val server: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null,
    val violations: Int = 1
)
