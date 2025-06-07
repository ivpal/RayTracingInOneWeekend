package com.github.ivpal

import kotlin.math.sqrt
import kotlin.random.Random.Default.nextDouble

class Color(var r: Double, var g: Double, var b: Double) {
    operator fun plus(c: Color): Color = Color(r + c.r, g + c.g, b + c.b)

    operator fun plusAssign(c: Color) {
        r += c.r
        g += c.g
        b += c.b
    }

    operator fun times(d: Double): Color = Color(r * d, g * d, b * d)

    operator fun times(c: Color): Color = Color(r * c.r, g * c.g, b * c.b)

    override fun toString(): String {
        return "Color(r=$r, g=$g, b=$b)"
    }

    companion object {
        fun random(): Color = Color(nextDouble(), nextDouble(), nextDouble())

        fun random(from: Double, to: Double): Color =
            Color(nextDouble(from, to), nextDouble(from, to), nextDouble(from, to))
    }
}

fun linearToGamma(linearComponent: Double): Double {
    if (linearComponent > 0) {
        return sqrt(linearComponent)
    }
    return 0.0
}
