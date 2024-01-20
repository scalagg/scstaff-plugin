package gg.scala.staff.anticheat.sampling

/**
 * @author GrowlyX
 * @since 1/20/2024
 */
class SixtySecondSample(
    val tenSecondSamples: MutableList<TenSecondSample> = mutableListOf(),
    var current: TenSecondSample = TenSecondSample(),
    val lock: Any = Any()
)
{
    fun useCurrentSample(block: TenSecondSample.() -> Unit) = synchronized(lock)
    {
        current.block()
    }

    fun addToSampleAndReset() = synchronized(lock)
    {
        tenSecondSamples += current
        current = TenSecondSample()
    }

    fun medianOf() = tenSecondSamples
        .flatMap { it.samples.toList() }
        .internalMedian()

    fun accumulatedMedianOf() = tenSecondSamples
        .map { it.samples.sum() }
        .internalMedian()

    fun List<Int>.internalMedian() = sorted()
        .let {
            if (it.size % 2 == 0)
                (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
            else
                it[it.size / 2]
        }
}
