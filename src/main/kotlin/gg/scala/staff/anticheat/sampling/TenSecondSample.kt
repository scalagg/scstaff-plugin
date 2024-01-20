package gg.scala.staff.anticheat.sampling

import com.google.common.collect.EvictingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Takes an evicting ten-second sample of anticheat violations
 * continuously run during a match.
 *
 * @author GrowlyX
 * @since 1/20/2024
 */
data class TenSecondSample(
    val samples: EvictingQueue<Int> = EvictingQueue.create<Int>(10)
        .apply {
               repeat(10) { add(0) }
        },
    var buffer: AtomicInteger = AtomicInteger(0)
)
{
    fun triggerAlert()
    {
        buffer.addAndGet(1)
    }

    /**
     * Evicts the eldest entry in the [samples] queue and adds the
     * latest sample from [buffer] to the queue.
     */
    fun resetAndAddToSample()
    {
        val sample = buffer.get()
        samples.add(sample)
    }
}
