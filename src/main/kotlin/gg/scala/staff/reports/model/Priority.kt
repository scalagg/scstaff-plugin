package gg.scala.staff.reports.model

import com.cryptomorin.xseries.XMaterial

enum class Priority(
    val iconMaterial: XMaterial
)
{
    Normal(XMaterial.PAPER),
    High(XMaterial.REDSTONE_BLOCK)
}
