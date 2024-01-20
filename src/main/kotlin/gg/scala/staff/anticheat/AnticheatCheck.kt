package gg.scala.staff.anticheat

/**
 * @author GrowlyX
 * @since 12/24/2023
 */
enum class AnticheatCheck(val displayName: String)
{
    AUTO_CLICKER("Auto Clicker"),
    DOUBLE_CLICK("Double Click"),
    AIM("Aim"),
    CLIENT("Client"),
    PACKETS("Packets"),
    SPEED("Speed"),
    UNEXPECTED("Unexpected"),
    FLY("Fly"),
    REACH("Reach"),
    TIMER("Timer"),
    VELOCITY("Velocity")
}
