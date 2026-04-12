package gg.scala.staff.anticheat.sampling

import com.google.common.collect.EvictingQueue
import java.util.concurrent.atomic.AtomicInteger

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

    fun resetAndAddToSample()
    {
        val sample = buffer.get()
        samples.add(sample)
    }
}
