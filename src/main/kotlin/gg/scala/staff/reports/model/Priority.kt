package gg.scala.staff.reports.model

import com.cryptomorin.xseries.XMaterial

/**
 * @author GrowlyX
 * @since 1/15/2024
 */
enum class Priority(
    val iconMaterial: XMaterial
)
{
    Normal(XMaterial.PAPER),
    High(XMaterial.REDSTONE_BLOCK)
}
