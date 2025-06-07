package com.github.ivpal

class Interval(val min: Double = Double.NEGATIVE_INFINITY, val max: Double = Double.POSITIVE_INFINITY) {
    val size: Double
        get() = max - min

    fun contains(d: Double): Boolean = d in min..max

    fun surrounds(d: Double): Boolean = min < d && d < max

    fun clamp(d: Double): Double =
        when {
            d < min -> min
            d > max -> max
            else -> d
        }
}

val EMPTY = Interval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
val UNIVERSE = Interval()
