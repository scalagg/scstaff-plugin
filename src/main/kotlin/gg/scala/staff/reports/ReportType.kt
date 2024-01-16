package gg.scala.staff.reports

import com.cryptomorin.xseries.XMaterial

/**
 * @author GrowlyX
 * @since 9/6/2021
 */
enum class ReportType(
    val fancyName: String,
    val examples: List<String>,
    val material: XMaterial,
) {

    CombatHacks(
        "Combat Hacks",
        listOf("KillAura", "Reach", "Aim Assist"),
        XMaterial.DIAMOND_SWORD
    ),
    MovementHacks(
        "Movement Hacks",
        listOf("Speed", "Bunny Hop", "Fly"),
        XMaterial.RABBIT_FOOT
    ),
    VelocityHacks(
        "Velocity Hacks",
        listOf("Velocity", "Reduced KB", "Anti KB"),
        XMaterial.SLIME_BALL
    ),
    ChatAbuse(
        "Chat Abuse",
        listOf("Toxicity", "Spam", "Illegal Characters"),
        XMaterial.PAPER
    ),
    GameSabotage(
        "Game Sabotage",
        listOf("Camping", "Running", "Stalling"),
        XMaterial.GLOWSTONE_DUST
    ),
    CrossTeaming(
        "Teaming",
        listOf("Trucing", "Player Collision", "Group Game Sabotage"),
        XMaterial.STICK
    ),
    SuspiciousActivity(
        "Suspicious Activity",
        listOf("DDoS Threats", "Dox Threats", "Self Harm"),
        XMaterial.RED_DYE
    ),
    Other(
        "Other",
        listOf("Anything that's not listed..."),
        XMaterial.PAINTING
    )
}
