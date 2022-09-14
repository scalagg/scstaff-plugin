package gg.scala.staff.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import java.util.*

/**
 * @author GrowlyX
 * @since 9/10/2022
 */
@Service
object StaffProfileService : ProfileOrchestrator<StaffProfile>()
{
    override fun new(uniqueId: UUID) = StaffProfile(uniqueId)
    override fun type() = StaffProfile::class
}
